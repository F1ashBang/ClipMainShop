package com.example.clipmainjava;

import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.clipmainjava.Adapters.ImagePagerAdapter;
import com.example.clipmainjava.Adapters.SizeChipAdapter;
import com.example.clipmainjava.CartRealize.CartManager;
import com.example.clipmainjava.Models.ImageModel;
import com.example.clipmainjava.Models.ProductModel;
import com.example.clipmainjava.Models.SizeModel;
import com.tbuonomo.viewpagerdotsindicator.SpringDotsIndicator;

import java.util.ArrayList;
import java.util.List;

public class ItemViewActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private SpringDotsIndicator dotsIndicator;
    private TextView textViewTitle, textViewPrice, textViewDescription, textViewTitleTop;
    private RecyclerView recyclerViewSizes;
    private SizeChipAdapter sizeAdapter;
    private String selectedSize = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_item_view);

        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String imagesJson = getIntent().getStringExtra("imagesJson");
        List<ImageModel> images = ImageModel.parseJsonArray(imagesJson);

        viewPager = findViewById(R.id.viewPagerItems);
        dotsIndicator = findViewById(R.id.dotsIndicator);
        textViewTitle = findViewById(R.id.textViewItemTitleInItem);
        textViewPrice = findViewById(R.id.textViewItemPriceInItem);
        textViewDescription = findViewById(R.id.textViewItemDescriptionInItem);
        textViewTitleTop = findViewById(R.id.textViewItemTitleTopInItem);
        recyclerViewSizes = findViewById(R.id.recycleViewSizesInItem);

        textViewTitleTop.setText(title);
        textViewDescription.setText(description);
        textViewPrice.setText(String.format("%,.0f ₽", price));
        textViewTitle.setText(title);

        String sizesJson = getIntent().getStringExtra("sizesJson");
        List<SizeModel> sizes = SizeModel.parseJsonToArray(sizesJson);

        if (sizes != null && !sizes.isEmpty()) {
            setupSizeChips(sizes);
        }


        if (images != null && !images.isEmpty()) {
            ImagePagerAdapter adapter = new ImagePagerAdapter(images);
            viewPager.setAdapter(adapter);
            dotsIndicator.setViewPager2(viewPager);
        } else {
            dotsIndicator.setVisibility(View.GONE);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setupSizeChips(List<SizeModel> sizes) {
        sizeAdapter = new SizeChipAdapter(sizes, (size, position) -> {
            selectedSize = size.getName();
            Toast.makeText(ItemViewActivity.this,
                    "Выбран: " + selectedSize, Toast.LENGTH_SHORT).show();
        });

        recyclerViewSizes.setAdapter(sizeAdapter);
    }

    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    public void Click_buttonBackToShop(View view) {
        finish();
    }

    public void Click_buttonAddToBasket(View view) {
        // Проверяем, выбран ли размер
        if (selectedSize == null) {
            Toast.makeText(this, "Выберите размер", Toast.LENGTH_SHORT).show();
            return;
        }

        long id = getIntent().getLongExtra("id", 0);
        String title = getIntent().getStringExtra("title");
        double price = getIntent().getDoubleExtra("price", 0.0);
        String description = getIntent().getStringExtra("description");
        String imagesJson = getIntent().getStringExtra("imagesJson");

        ProductModel product = new ProductModel();
        product.setId(id);
        product.setTitle(title);
        product.setPrice(price);
        product.setDescription(description);

        List<ImageModel> images = ImageModel.parseJsonArray(imagesJson);
        product.setImages(images);

        CartManager cartManager = CartManager.getInstance(this);
        cartManager.add(product, 1, selectedSize);

        Toast.makeText(this, title + " (" + selectedSize + ") добавлен в корзину!",
                Toast.LENGTH_SHORT).show();
    }
}