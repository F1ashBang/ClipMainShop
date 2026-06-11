using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ClipMain.Models
{
    [Table("images")]
    public class Image
    {
        [Key][Column("id")]
        public long Id { get; set; }

        [Column("file_name")]
        public string FileName { get; set; }

        [Column("file_path")]
        public string FilePath { get; set; }

        [Column("uploaded_at")]
        public DateTime? UploadedAt { get; set; }

        [Column("product_id")]
        public long ProductId { get; set; }

        [ForeignKey("ProductId")]
        public Product product { get; set; }
    }
}
