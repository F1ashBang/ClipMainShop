using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

namespace ClipMain.Models
{
    public class OrderModel
    {
        [JsonPropertyName("id")]
        public long Id { get; set; }

        [JsonPropertyName("status")]
        public string Status { get; set; }

        [JsonPropertyName("totalPrice")]
        public double TotalPrice { get; set; }

        [JsonPropertyName("createdAt")]
        public string CreatedAt { get; set; }

        [JsonPropertyName("firstName")]
        public string FirstName { get; set; }

        [JsonPropertyName("lastName")]
        public string LastName { get; set; }

        [JsonPropertyName("address")]
        public string Address { get; set; }

        [JsonPropertyName("phoneAtOrder")]
        public string PhoneAtOrder { get; set; }
        [JsonPropertyName("cancellationReason")]
        public string CancellationReason { get; set; }
        [JsonPropertyName("items")]
        public List<object> Items { get; set; }

        public int ItemsCount => Items != null ? Items.Count : 0;

        public string TotalPriceString
        {
            get
            {
                if (TotalPrice % 1 == 0)
                    return TotalPrice.ToString("N0") + " ₽";
                return TotalPrice.ToString("N2") + " ₽";
            }
        }
        public string ItemsDisplay
        {
            get
            {
                if (Items == null || Items.Count == 0) return "Нет товаров";

                var sb = new System.Text.StringBuilder();
                foreach (var item in Items)
                {
                    if (item is JsonElement json)
                    {
                        string title = json.TryGetProperty("productTitle", out var t) ? t.GetString() : "?";
                        int qty = json.TryGetProperty("quantity", out var q) && q.TryGetInt32(out int qi) ? qi : 0;
                        string price = json.TryGetProperty("productPrice", out var p) ? p.GetString() : "0";

                        sb.AppendLine($"{title} x{qty} — {price} ₽");
                    }
                }
                return sb.ToString().TrimEnd();
            }
        }

    }
}
