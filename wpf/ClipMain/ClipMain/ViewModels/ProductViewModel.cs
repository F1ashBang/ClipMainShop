using ClipMain.Models;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.IO;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace ClipMain.VIewModels
{
    public class ProductViewModel : INotifyPropertyChanged
    {
        private readonly Models.Product _product;
        private List<Sizes> _sizes;

        public ProductViewModel(Models.Product product)
        {
            _product = product;
        }

        public long Id => _product.Id;
        public string Title => _product.Title;
        public string Description => _product.Description;
        public List<Models.Sizes> Sizes
        {
            get => _sizes;
            set
            {
                _sizes = value;
                OnPropertyChanged(nameof(SizesString));
                OnPropertyChanged(nameof(StockString));
            }
        }
        public string StockString
        {
            get
            {
                if (Sizes == null || Sizes.Count == 0)
                    return "Нет в наличии";
                int total = Sizes.Sum(s => s.Quantity);
                if (total == 0) return "Нет в наличии";
                return $"В наличии: {total} шт.";
            }
        }
        public string SizesString
        {
            get
            {
                if (Sizes == null || Sizes.Count == 0)
                    return "Нет размеров";
                return string.Join(", ", Sizes.Select(s => $"{s.Name} ({s.Quantity} шт.)"));
            }
        }

        public string PriceString 
        { 
            get
            {
                if (double.TryParse(_product.Price, out double price))
                {
                    return string.Format("{0:0.00 ₽}", price);
                }
                return _product.Price + " ₽";
            }
        }

        public string FirstImagePath
        {
            get
            {
                if (_product.Images != null && _product.Images.Count > 0)
                {
                    string fileName = _product.Images[0].FileName;
                    if (fileName == null)
                    {
                        fileName = _product.Images[0].FilePath;
                    }

                    string fullPath = System.IO.Path.Combine(@"C:\uploads", fileName);

                    if (System.IO.File.Exists(fullPath))
                        return fullPath;
                }
                return null;
            }
        }

        public Models.Product Product => _product;

        public event PropertyChangedEventHandler PropertyChanged;
        protected void OnPropertyChanged([CallerMemberName] string name = null)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
        }
    }
}
