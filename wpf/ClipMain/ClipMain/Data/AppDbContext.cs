using ClipMain.Models;
using Microsoft.EntityFrameworkCore;
using Npgsql;
using Dapper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;

namespace ClipMain.Data
{
    public class AppDbContext : DbContext
    {
        public DbSet<Product> Products { get; set; }
        public DbSet<Image> Images { get; set; }
        public DbSet<Sizes> Sizes { get; set; }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            optionsBuilder.UseNpgsql(
                "Host=localhost;Port=5432;Database=phone_verification;Username=postgres;Password=Bfhbfhbrb&454"
                );
        }

        public List<Sizes> GetSizesForProduct(long productId)
        {
            var sizes = new List<Sizes>();

            using (var connection = new NpgsqlConnection(
                "Host=localhost;Port=5432;Database=phone_verification;Username=postgres;Password=Bfhbfhbrb&454"))
            {
                connection.Open();

                using (var command = new NpgsqlCommand(@"
            SELECT s.id, s.name, s.category, s.sort_order, 
                   COALESCE(ps.quantity, 0) as quantity
            FROM sizes s 
            INNER JOIN product_sizes ps ON s.id = ps.size_id 
            WHERE ps.product_id = @productId
            ORDER BY s.sort_order", connection))
                {
                    command.Parameters.AddWithValue("@productId", productId);

                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            sizes.Add(new Sizes
                            {
                                Id = reader.GetInt64(0),
                                Name = reader.GetString(1),
                                Category = reader.IsDBNull(2) ? null : reader.GetString(2),
                                SortOrder = reader.GetInt32(3),
                                Quantity = reader.GetInt32(4)
                            });
                        }
                    }
                }
            }

            return sizes;
        }
    }
}
