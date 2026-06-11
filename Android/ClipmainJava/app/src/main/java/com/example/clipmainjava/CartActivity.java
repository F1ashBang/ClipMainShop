package com.example.clipmainjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clipmainjava.Adapters.CartAdapter;
import com.example.clipmainjava.CartRealize.CartManager;
import com.example.clipmainjava.Models.CartItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter adapter;
    private CartManager cartManager;
    private TextView textViewItemCount, textViewTotalPrice;
    private Button buttonCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cart);

        recyclerView = findViewById(R.id.recyclerViewCart);
        textViewItemCount = findViewById(R.id.textViewItemCountInCart);
        textViewTotalPrice = findViewById(R.id.textViewTotalPriceCart);
        buttonCheckout = findViewById(R.id.buttonCheckoutCart);

        cartManager = CartManager.getInstance(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        loadCart();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    protected void onResume() {
        super.onResume();
        loadCart();
    }

    private void loadCart() {
        List<CartItem> items = cartManager.getItems();

        if (items.isEmpty()) {
            textViewItemCount.setText("0 торавров");
            textViewTotalPrice.setText("0 ₽");
            buttonCheckout.setEnabled(false);
            buttonCheckout.setText("Корзина пуста");
        }
        else {
            int totalCount = 0;
            for (CartItem item : items) {
                totalCount += item.getQuantity();
            }
            textViewItemCount.setText(totalCount + " " + getProductWord(totalCount));
            textViewTotalPrice.setText(formatPrice(cartManager.getTotalPrice()));
            buttonCheckout.setEnabled(true);
            buttonCheckout.setText("Оформить заказ");
        }

        adapter = new CartAdapter(items, new CartAdapter.CartListener() {
            @Override
            public void onQuantityChanged(Long productId, int newQuantity) {
                cartManager.setQuantity(productId, newQuantity);
                loadCart();
            }

            @Override
            public void onItemRemoved(Long productId) {
                cartManager.remove(productId);
                loadCart();
                Toast.makeText(CartActivity.this, "Товар удалён", Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private String getProductWord(int count) {
        int lastDigit = count % 10;
        int lastTwo = count % 100;

        if (lastTwo >= 11 && lastTwo <= 19) return "товаров";
        if (lastDigit == 1) return "товар";
        if (lastDigit >= 2 && lastDigit <= 4) return "товара";
        return "товаров";
    }

    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("ru"));
        return formatter.format(price) + " ₽";
    }

    public void Click_checkout(View view) {
        if (cartManager.getItems().isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
        startActivity(intent);

    }
    public void Click_buttonGoToCartFromCart(View view) {

    }

    public void Click_buttonGoToProfileFromCart(View view) {
        Intent intent = new Intent(CartActivity.this, ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    public void Click_buttonGoToMainFromCart(View view) {
        Intent intent = new Intent(this, MainShopActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    public void Click_clearCart(View view) {
        if (cartManager.getItems().isEmpty()) {
            Toast.makeText(this, "Корзина уже пуста", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Очистить корзину?")
                .setMessage("Все товары будут удалены")
                .setPositiveButton("Да", (dialog, which) -> {
                    cartManager.clear();
                    loadCart();
                    Toast.makeText(CartActivity.this, "Корзина очищена", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Отмена", null)
                .show();
    }
}