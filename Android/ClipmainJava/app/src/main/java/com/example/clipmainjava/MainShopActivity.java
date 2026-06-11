package com.example.clipmainjava;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clipmainjava.Adapters.ImageAdapter;
import com.example.clipmainjava.Adapters.ProductAdapter;
import com.example.clipmainjava.Adapters.StaticHeaderAdapter;
import com.example.clipmainjava.Database.ApiClient;
import com.example.clipmainjava.Database.ApiService;
import com.example.clipmainjava.Models.ImageModel;
import com.example.clipmainjava.Models.ProductModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainShopActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private Handler searchHandler = new Handler();
    private Runnable searchRunnable;
    private View searchHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main_shop);

        recyclerView = findViewById(R.id.recycleViewMainShop);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchHeader = LayoutInflater.from(this).inflate(R.layout.header_search, recyclerView, false);
        SearchView searchView = searchHeader.findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() >= 2) {
                    searchHandler.removeCallbacks(searchRunnable);
                    searchRunnable = () -> searchProducts(newText);
                    searchHandler.postDelayed(searchRunnable, 300);
                }
                else if (newText.isEmpty()) {
                    loadProducts();
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProducts(query);
                return true;
            }
        });

        loadProducts();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
    }

    private void loadProducts() {
        ApiService service = ApiClient.getService();

        service.getAllProducts().enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<ProductModel> products = response.body();
                    adapter = new ProductAdapter(response.body());
                    ConcatAdapter concatAdapter = new ConcatAdapter(
                            new StaticHeaderAdapter(searchHeader), adapter
                    );
                    recyclerView.setAdapter(concatAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(MainShopActivity.this, "Ошибка подключения к серверу", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Click_buttonGoToBasketFromMainShop(View view) {
        Intent intent = new Intent(this, CartActivity.class);
        startActivity(intent);
        finish();
    }

    public void Click_buttonGoToProfileFromMainShop(View view) {
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void Click_buttonGoToMainFromMainShop(View view) {
    }

    private void searchProducts(String query) {
        ApiService service = ApiClient.getService();
        service.searchProducts(query).enqueue(new Callback<List<ProductModel>>() {
            @Override
            public void onResponse(Call<List<ProductModel>> call, Response<List<ProductModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new ProductAdapter(response.body());
                    ConcatAdapter concatAdapter = new ConcatAdapter(
                      new StaticHeaderAdapter(searchHeader), adapter
                    );
                    recyclerView.setAdapter(concatAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<ProductModel>> call, Throwable t) {
                Toast.makeText(MainShopActivity.this, "Ошибка поиска", Toast.LENGTH_SHORT).show();
            }
        });
    }
}