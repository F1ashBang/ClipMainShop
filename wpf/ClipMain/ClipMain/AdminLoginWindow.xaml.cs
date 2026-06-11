using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
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
using System.Windows.Shapes;
using System.Windows.Threading;

namespace ClipMain
{
    /// <summary>
    /// Логика взаимодействия для AdminLoginWindow.xaml
    /// </summary>
    public partial class AdminLoginWindow : Window
    {
        private static readonly HttpClient client = new HttpClient
        {
            BaseAddress = new Uri("http://localhost:8080/")
        };

        private const string ADMIN_API_KEY = "P@ssW0rd!xK7mP2vL9nQ4wR8hT3jB6y";
        private const string DEV_CODE = "1234";

        private DispatcherTimer _timer;
        private int _secondsLeft;
        private bool _isDevMode = false;

        public bool IsLoggedIn { get; private set; }


        public AdminLoginWindow()
        {
            InitializeComponent();
            client.DefaultRequestHeaders.Clear();
            client.BaseAddress = new Uri("http://localhost:8080/");
            client.DefaultRequestHeaders.Add("X-Admin-Key", ADMIN_API_KEY);

            _timer = new DispatcherTimer();
            _timer.Interval = TimeSpan.FromSeconds(1);
            _timer.Tick += Timer_Tick;
        }

        private void PhoneBox_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e)
        {
            CodeBox.Text = "";
            LoginButton.IsEnabled = false;
            StopTimer();
        }

        private async void SendCodeButton_Click(object sender, RoutedEventArgs e)
        {
            string phone = PhoneBox.Text.Trim();

            if (string.IsNullOrEmpty(phone) || phone.Length != 11)
            {
                StatusText.Text = "Введите корректный номер (11 цифр)";
                return;
            }

            // Режим разработчика
            if (_isDevMode)
            {
                StatusText.Text = "Режим разработчика: код 1234";
                LoginButton.IsEnabled = true;
                return;
            }

            StatusText.Text = "Отправка кода...";
            SendCodeButton.IsEnabled = false;

            try
            {
                var data = new { phone = phone };
                var json = JsonSerializer.Serialize(data);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var response = await client.PostAsync("/login", content);

                if (response.IsSuccessStatusCode)
                {
                    StatusText.Text = "Код отправлен! Действителен 60 секунд";
                    CodeBox.Focus();
                    LoginButton.IsEnabled = true;
                    StartTimer(60);
                }
                else
                {
                    StatusText.Text = "Ошибка при отправке кода";
                    SendCodeButton.IsEnabled = true;
                }
            }
            catch (Exception)
            {
                StatusText.Text = "Сервер недоступен. Проверьте подключение.";
                SendCodeButton.IsEnabled = true;
            }
        }

        private async void LoginButton_Click(object sender, RoutedEventArgs e)
        {
            string phone = PhoneBox.Text.Trim();
            string code = CodeBox.Text.Trim();

            if (string.IsNullOrEmpty(phone) || string.IsNullOrEmpty(code))
            {
                StatusText.Text = "Введите номер телефона и код";
                return;
            }

            if (_isDevMode)
            {
                if (code == DEV_CODE)
                { 
                    IsLoggedIn = true;

                    var mainWindow = new MainWindow();
                    mainWindow.Show();

                    this.Close();
                    return;
                }
                else
                {
                    StatusText.Text = "Неверный код разработчика";
                    return;
                }
            }

            StatusText.Text = "Проверка кода...";

            try
            {
                var data = new { phone = phone, code = code };
                var json = JsonSerializer.Serialize(data);
                var content = new StringContent(json, Encoding.UTF8, "application/json");

                var response = await client.PostAsync("/admin/login", content);

                if (response.IsSuccessStatusCode)
                {
                    IsLoggedIn = true;
                    StopTimer();

                    var mainWindow = new MainWindow();
                    mainWindow.Show();

                    this.Close();
                }
                else
                {
                    StatusText.Text = "Неверный код или доступ запрещён";
                    CodeBox.Text = "";
                    CodeBox.Focus();
                }
            }
            catch (Exception)
            {
                StatusText.Text = "Ошибка подключения к серверу";
            }
        }
        private void StartTimer(int seconds)
        {
            _secondsLeft = seconds;
            UpdateTimerText();
            _timer.Start();
        }

        private void StopTimer()
        {
            _timer.Stop();
            TimerText.Text = "";
        }

        private void Timer_Tick(object sender, EventArgs e)
        {
            _secondsLeft--;
            UpdateTimerText();

            if (_secondsLeft <= 0)
            {
                StopTimer();
                CodeBox.IsEnabled = false;
                LoginButton.IsEnabled = false;
                SendCodeButton.IsEnabled = true;
                StatusText.Text = "Время действия кода истекло. Запросите новый.";
            }
        }

        private void UpdateTimerText()
        {
            TimerText.Text = $"{_secondsLeft} сек";
            if (_secondsLeft <= 10)
            {
                TimerText.Foreground = System.Windows.Media.Brushes.Red;
            }
            else
            {
                TimerText.Foreground = System.Windows.Media.Brushes.Gray;
            }
        }

        private void DevModeCheckBox_Checked(object sender, RoutedEventArgs e)
        {
            _isDevMode = true;
            CodeBox.Text = "";
            LoginButton.IsEnabled = true;
            StopTimer();
            SendCodeButton.IsEnabled = false;
            StatusText.Text = "Режим разработчика включён. Используйте код 1234";
        }

        private void DevModeCheckBox_Unchecked(object sender, RoutedEventArgs e)
        {
            _isDevMode = false;
            CodeBox.Text = "";
            LoginButton.IsEnabled = false;
            SendCodeButton.IsEnabled = true;
            StatusText.Text = "Режим разработчика выключен";
        }
    }
}
