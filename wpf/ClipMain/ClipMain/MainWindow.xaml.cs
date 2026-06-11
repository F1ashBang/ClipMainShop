using ClipMain.Data;
using ClipMain.Models;
using ClipMain.VIewModels;
using Microsoft.EntityFrameworkCore;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Net.Http;
using System.Security.Policy;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace ClipMain
{
    /// <summary>
    /// Логика взаимодействия для MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private AppDbContext _context;
        private ObservableCollection<ProductViewModel> _products;
        
        private static readonly HttpClient client = new HttpClient
        {
            BaseAddress = new Uri("http://localhost:8080/")
        };

        private const string ADMIN_API_KEY = "P@ssW0rd!xK7mP2vL9nQ4wR8hT3jB6y";

        private List<User> _users;
        private List<OrderModel> _orders;
        private bool _ignoreStatusChange = false;

        public MainWindow()
        {
            InitializeComponent();
            client.DefaultRequestHeaders.Add("X-Admin-Key", ADMIN_API_KEY);
            _context = new AppDbContext();
            LoadProducts();
            LoadUsers();
            LoadOrders();
        }
        
        private void LoadProducts()
        {
            try
            {
                var products = _context.Products
                    .Include(p => p.Images)
                    .OrderByDescending(p => p.Id)
                    .ToList();

                _products = new ObservableCollection<ProductViewModel>();

                foreach (var product in products)
                {
                    var vm = new ProductViewModel(product);
                    vm.Sizes = _context.GetSizesForProduct(product.Id);  // ← загружаем через SQL
                    _products.Add(vm);
                }

                ProductsListView.ItemsSource = _products;
                StatusText.Text = $"🟢 Загружено {_products.Count} товаров";
            }
            catch (Exception ex)
            {
                StatusText.Text = "🔴 Ошибка: " + ex.Message;
            }
        }

        private void refreshButton_Click(object sender, RoutedEventArgs e)
        {
            LoadProducts();
        }

        private void addProduct_Click(object sender, RoutedEventArgs e)
        {
            var dialog = new ProductAddOrEditWindow();
            dialog.ShowDialog();

            if (dialog.IsSaved)
            {
                LoadProducts();
            }
        }

        private void editCardButton_Click(object sender, RoutedEventArgs e)
        {
            if (sender is Button button)
            {
                long id = 0;

                if (button.Tag is long tagLong)
                {
                    id = tagLong;
                }
                else if (button.Tag is string tagString && long.TryParse(tagString, out long parsedId))
                {
                    id = parsedId;
                }
                else
                {
                    if (button.DataContext is ProductViewModel vm)
                    {
                        id = vm.Id;
                    }
                    else
                    {
                        MessageBox.Show("Не удалось получить ID товара", "Ошибка",
                            MessageBoxButton.OK, MessageBoxImage.Error);
                        return;
                    }
                }

                var product = _context.Products
                    .Include(p => p.Images)
                    .FirstOrDefault(p => p.Id == id);

                if (product != null)
                {
                    var dialog = new ProductAddOrEditWindow(product);
                    dialog.ShowDialog();

                    if (dialog.IsSaved)
                    {
                        StatusText.Text = $"🟢 Товар \"{product.Title}\" обновлён";
                    }
                }
                else
                {
                    MessageBox.Show("Товар не найден в базе данных!", "Ошибка",
                        MessageBoxButton.OK, MessageBoxImage.Error);
                }
            }
        }

        private void deleteCardButton_Click(object sender, RoutedEventArgs e)
        {
            if (sender is Button button)
            {
                long id = 0;

                if (button.Tag is long tagLong)
                {
                    id = tagLong;
                }
                else if (button.Tag is string tagString && long.TryParse(tagString, out long parsedId))
                {
                    id = parsedId;
                }
                else if (button.DataContext is ProductViewModel vm)
                {
                    id = vm.Id;
                }
                else
                {
                    return;
                }

                var product = _context.Products.Find(id);
                if (product != null)
                {
                    var result = MessageBox.Show($"Удалить товар \"{product.Title}\"?",
                        "Подтверждение", MessageBoxButton.YesNo, MessageBoxImage.Warning);

                    if (result == MessageBoxResult.Yes)
                    {
                        _context.Products.Remove(product);
                        _context.SaveChanges();
                        LoadProducts();
                        StatusText.Text = $"🟢 Товар \"{product.Title}\" удалён";
                    }
                }
            }
        }

        private async void LoadUsers()
        {
            try
            {
                StatusText.Text = "🟡 Загрузка пользователей...";

                var response = await client.GetAsync("/admin/users");

                if (response.IsSuccessStatusCode)
                {
                    var json = await response.Content.ReadAsStringAsync();
                    _users = JsonSerializer.Deserialize<List<User>>(json,
                        new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

                    UsersListView.ItemsSource = _users;
                    StatusText.Text = $"🟢 Загружено {_users.Count} пользователей";
                }
                else
                {
                    StatusText.Text = "🔴 Ошибка загрузки пользователей";
                }
            }
            catch (Exception ex)
            {
                StatusText.Text = "🔴 Сервер недоступен: " + ex.Message;
            }
        }

        private void RefreshUsersButton_Click(object sender, RoutedEventArgs e)
        {
            LoadUsers();
        }

        private async void SaveUsersButton_Click(object sender, RoutedEventArgs e)
        {
            if (_users == null || _users.Count == 0)
            {
                MessageBox.Show("Нет данных для сохранения", "Информация",
                    MessageBoxButton.OK, MessageBoxImage.Information);
                return;
            }

            var result = MessageBox.Show("Сохранить изменения прав администраторов?",
                "Подтверждение", MessageBoxButton.YesNo, MessageBoxImage.Question);

            if (result != MessageBoxResult.Yes) return;

            try
            {
                StatusText.Text = "🟡 Сохранение...";
                int savedCount = 0;

                foreach (var user in _users)
                {
                    var data = new { IsAdmin = user.IsAdmin.ToString().ToLower() };
                    var json = JsonSerializer.Serialize(data);
                    var content = new StringContent(json, Encoding.UTF8, "application/json");

                    var response = await client.PutAsync($"/admin/users/{user.Id}", content);

                    if (response.IsSuccessStatusCode)
                    {
                        savedCount++;
                    }
                }

                StatusText.Text = $"🟢 Сохранено {savedCount} из {_users.Count}";
                MessageBox.Show($"Успешно сохранено: {savedCount} пользователей",
                    "Результат", MessageBoxButton.OK, MessageBoxImage.Information);
            }
            catch (Exception ex)
            {
                StatusText.Text = "🔴 Ошибка: " + ex.Message;
                MessageBox.Show("Ошибка: " + ex.Message, "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Error);
            }
        }
        private void Logout_Click(object sender, RoutedEventArgs e)
        {
            var result = MessageBox.Show("Выйти из админ-панели?",
                "Подтверждение", MessageBoxButton.YesNo, MessageBoxImage.Question);

            if (result == MessageBoxResult.Yes)
            {
                var loginWindow = new AdminLoginWindow();
                loginWindow.Show();
                this.Close();
            }
        }

        private void RefreshAllButton_Click(object sender, RoutedEventArgs e)
        {
            LoadUsers();
            LoadProducts();
            LoadOrders();
        }

        private async void LoadOrders()
        {
            try
            {
                StatusText.Text = "🟡 Загрузка заказов...";

                var response = await client.GetAsync("/orders/all");
                
                if (response.IsSuccessStatusCode)
                {
                    var json = await response.Content.ReadAsStringAsync();
                    _orders = JsonSerializer.Deserialize<List<OrderModel>>(json, new JsonSerializerOptions { PropertyNameCaseInsensitive = true });

                    ApplyOrderFilter();
                    StatusText.Text = $"🟢 Загружено {_orders.Count} заказов";
                }
                else
                {
                    StatusText.Text = "🔴 Ошибка загрузки заказов";
                }
            }
            catch (Exception ex)
            {
                StatusText.Text = "🔴 Сервер недоступен: " + ex.Message;
            }
        }

        private void ApplyOrderFilter()
        {
            if (_orders == null)
            {
                return;
            }

            var selectedItem = FilterStatus.SelectedItem as ComboBoxItem;
            string filter = selectedItem?.Content?.ToString() ?? "Все";

            if (filter == "Все")
            {
                OrdersListView.ItemsSource = _orders;
            }
            else
            {
                string status = null;
                switch (filter)
                {
                    case "Новые":
                        status = "new";
                        break;
                    case "В обработке":
                        status = "processing";
                        break;
                    case "В пути":
                        status = "shipped";
                        break;
                    case "Доставлен":
                        status = "delivered";
                        break;
                    case "Отменён":
                        status = "cancelled";
                        break;
                }

                OrdersListView.ItemsSource = _orders.Where(o => o.Status == status).ToList();
            }
        }

        private async void StatusCombo_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (_ignoreStatusChange) return;
            if (e.AddedItems.Count == 0) return;

            if (sender is ComboBox comboBox && comboBox.Tag != null)
            {
                var selectedItem = comboBox.SelectedItem as ComboBoxItem;
                if (selectedItem?.Tag == null) return;

                long orderId = long.Parse(comboBox.Tag.ToString());
                string newStatus = selectedItem.Tag.ToString();

                var order = _orders?.FirstOrDefault(o => o.Id == orderId);
                if (order == null) return;

                if (order.Status == newStatus) return;

                try
                {
                    StatusText.Text = $"🟡 Обновление статуса заказа №{orderId}...";

                    var data = new { status = newStatus };
                    string json = JsonSerializer.Serialize(data);
                    StringContent content = new StringContent(json, Encoding.UTF8, "application/json");

                    var response = await client.PutAsync($"/orders/{orderId}/status", content);

                    if (response.IsSuccessStatusCode)
                    {
                        order.Status = newStatus;
                        StatusText.Text = $"🟢 Статус заказа №{orderId} изменён на {newStatus}";


                        _ignoreStatusChange = true;
                        ApplyOrderFilter();
                        _ignoreStatusChange = false;
                    }
                    else
                    {
                        StatusText.Text = "🔴 Ошибка изменения статуса";
                        _ignoreStatusChange = true;
                        comboBox.SelectedValue = order.Status;
                        _ignoreStatusChange = false;
                    }
                }
                catch (Exception ex)
                {
                    StatusText.Text = "🔴 Ошибка: " + ex.Message;
                }
            }
        }

        private void FilterStatus_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            ApplyOrderFilter();
        }

        private void RefreshOrders_Click(object sender, RoutedEventArgs e)
        {
            LoadOrders();
        }
    }
}
