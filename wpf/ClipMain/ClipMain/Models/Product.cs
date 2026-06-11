using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Controls;

namespace ClipMain.Models
{
    [Table("products")]
    public class Product
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Required]
        [Column("title")]
        public string Title { get; set; }
        [Column("price")]
        public string Price { get; set; }
        [Column("description")]
        public string Description { get; set; }

        public List<Image> Images { get; set; } = new List<Image>();
    }
}
