using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace ClipMain.Models
{
    public class OrderItemModel
    {
        [JsonPropertyName("productTitle")]
        public string ProductTitle { get; set; }

        [JsonPropertyName("productPrice")]
        public string ProductPrice { get; set; }

        [JsonPropertyName("size")]
        public string Size { get; set; }

        [JsonPropertyName("quantity")]
        public int Quantity { get; set; }

        [JsonPropertyName("imageUrl")]
        public string ImageUrl { get; set; }

        public OrderItemModel() { }
    }
}
