using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClipMain.Models
{
    [Table("sizes")]
    public class Sizes
    {
        [Key]
        [Column("id")]
        public long Id { get; set; }

        [Required]
        [Column("name")]
        public string Name { get; set; }

        [Column("category")]
        public string Category { get; set; }

        [Column("sort_order")]
        public int SortOrder { get; set; }

        [NotMapped]
        public int Quantity { get; set; } = 0;
        
    }
}
