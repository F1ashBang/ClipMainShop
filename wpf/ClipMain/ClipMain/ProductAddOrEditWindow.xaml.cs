using ClipMain.Data;
using ClipMain.Models;
using ClipMain.ViewModels;
using Microsoft.EntityFrameworkCore;
using Microsoft.Win32;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;

namespace ClipMain
{
    /// <summary>
    /// Логика взаимодействия для ProductAddOrEditWindow.xaml
    /// </summary>
    public partial class ProductAddOrEditWindow : Window
    {
        private ObservableCollection<ImagePreview> _selectedImages;
        private List<SizeViewModel> _sizeViewModel;

        public Product Product { get; private set; }

        private bool _isEditing;

        private const string UPLOAD_DIR = @"C:\uploads\";

        public bool IsSaved { get; private set; }
        private AppDbContext _context;

        public ProductAddOrEditWindow()
        {
            InitializeComponent();

            _context = new AppDbContext();
            _selectedImages = new ObservableCollection<ImagePreview>();
            ImagesListBox.ItemsSource = _selectedImages;

            Product = new Product();
            _isEditing = false;

            Title = "Добавить товар";

            LoadSizes();
        }
        public ProductAddOrEditWindow(Product product)
        {
            InitializeComponent();
            _selectedImages = new ObservableCollection<ImagePreview>();
            ImagesListBox.ItemsSource = _selectedImages;
            Product = product;
            _isEditing = true;
            _context = new AppDbContext();

            TBoxTitle.Text = product.Title;
            TBoxPrice.Text = product.Price;
            TBoxDescription.Text = product.Description;

            if (product.Images != null)
            {
                foreach (var image in product.Images)
                {
                    string name = image.FileName;
                    if (name == null) name = image.FilePath;

                    string fullPath = Path.Combine(UPLOAD_DIR, name);
                    _selectedImages.Add(new ImagePreview
                    {
                        FileName = name,
                        FilePath = fullPath,
                        PreviewPath = File.Exists(fullPath) ? fullPath : null
                    });
                }
            }

            Title = "Редактировать товар";
            
            LoadSizes();
        }

        private void LoadSizes()
        {
            var allSizes = _context.Sizes
                .OrderBy(s => s.Category)
                .ThenBy(s => s.SortOrder)
                .ToList();

            List<Sizes> productSizes = new List<Sizes>();
            if (_isEditing)
            {
                productSizes = _context.GetSizesForProduct(Product.Id);
            }

            _sizeViewModel = allSizes.Select(s =>
            {
                var existing = productSizes.FirstOrDefault(ps => ps.Id == s.Id);
                return new SizeViewModel
                {
                    Id = s.Id,
                    Name = s.Name,
                    Category = s.Category,
                    Quantity = existing?.Quantity ?? 0,
                    IsSelected = existing != null
                };
            }).ToList();

            SizesItemsControl.ItemsSource = _sizeViewModel;
        }

        private void ProcessSizes()
        {
            _context.Database.ExecuteSqlRaw(
                    "DELETE FROM product_sizes WHERE product_id = {0}", 
                    Product.Id
                    );

            foreach (var svm in _sizeViewModel)
            {
                if (svm.IsSelected)
                { 
                    _context.Database.ExecuteSqlRaw(
                        "INSERT INTO product_sizes (product_id, size_id, quantity) VALUES ({0}, {1}, {2})",
                        Product.Id, svm.Id, svm.Quantity);
                }
            }
        }

        private void TBoxPrice_PreviewTextInput(object sender, TextCompositionEventArgs e)
        {
            e.Handled = !char.IsDigit(e.Text, 0) && e.Text != "," && e.Text != ".";

            if ((e.Text == "," || e.Text == ".") &&
                (TBoxPrice.Text.Contains(",") || TBoxPrice.Text.Contains(".")))
            {
                e.Handled = true;
            }
        }

        private void removeImageButton_Click(object sender, RoutedEventArgs e)
        {
            if (sender is FrameworkElement element && element.Tag is string filePath)
            {
                var imageToRemove = _selectedImages.FirstOrDefault(img => img.FilePath == filePath);
                if (imageToRemove != null)
                {
                    _selectedImages.Remove(imageToRemove);
                }
            }
        }

        private void browseImageButton_Click(object sender, RoutedEventArgs e)
        {
            OpenFileDialog openFileDialog = new OpenFileDialog
            {
                Multiselect = true,
                Filter = "Изображения|*.jpg;*.jpeg;*.png;*.bmp;*.gif|Все файлы|*.*",
                Title = "Выберите фотографии товара"
            };

            if (openFileDialog.ShowDialog() == true)
            {
                foreach (string filePath in openFileDialog.FileNames)
                {
                    if (!_selectedImages.Any(img => img.FilePath == filePath))
                    {
                        _selectedImages.Add(new ImagePreview
                        {
                            FileName = Path.GetFileName(filePath),
                            FilePath = filePath,
                            PreviewPath = filePath
                        });
                    }
                }
            }
        }

        private void saveButton_Click(object sender, RoutedEventArgs e)
        {
            if (string.IsNullOrWhiteSpace(TBoxTitle.Text))
            {
                MessageBox.Show("Введите название товара!", "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Warning);
                TBoxTitle.Focus();
                return;
            }

            if (string.IsNullOrWhiteSpace(TBoxPrice.Text))
            {
                MessageBox.Show("Введите корректную цену!", "Ошибка",
                    MessageBoxButton.OK, MessageBoxImage.Warning);
                TBoxPrice.Focus();
                return;
            }

            Product.Title = TBoxTitle.Text.Trim();
            Product.Price = TBoxPrice.Text.Trim();
            Product.Description = TBoxDescription.Text != null ? TBoxDescription.Text.Trim() : null;

            if (!_isEditing)
            {
                _context.Products.Add(Product);
            }
            _context.SaveChanges();

            ProcessSizes();
            ProcessImages();

            _context.SaveChanges();

            IsSaved = true;
            Close();
        }

        private void ProcessImages()
        {
            if (!Directory.Exists(UPLOAD_DIR))
            {
                Directory.CreateDirectory(UPLOAD_DIR);
            }

            if (_isEditing)
            {
                Product.Images.Clear();
            }

            foreach (var imagePreview in _selectedImages)
            {
                string fileName = Path.GetFileName(imagePreview.FilePath);
                string destinationPath = Path.Combine(UPLOAD_DIR, fileName);

                if (!File.Exists(destinationPath) || imagePreview.FilePath != destinationPath)
                {
                    File.Copy(imagePreview.FilePath, destinationPath, overwrite: true);
                }

                Product.Images.Add(new Image
                {
                    FileName = fileName,
                    FilePath = fileName,
                    UploadedAt = DateTime.Now
                });
            }
        }


        private void cancelButton_Click(object sender, RoutedEventArgs e)
        {
            DialogResult = false;
            Close();
        }
    }

    public class ImagePreview
    {
        public string FileName { get; set; }
        public string FilePath { get; set; }
        public string PreviewPath { get; set; }
    }
}
