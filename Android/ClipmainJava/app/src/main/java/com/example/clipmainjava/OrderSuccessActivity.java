package com.example.clipmainjava;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class OrderSuccessActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_succes);

        String orderId = getIntent().getStringExtra("orderId");

        TextView textViewOrderId = findViewById(R.id.textViewOrderId);
        textViewOrderId.setText("Заказ № " + orderId);

        Button buttonGoToShop = findViewById(R.id.buttonToShop);
        buttonGoToShop.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainShopActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

        Button buttonGoToOrders = findViewById(R.id.buttonToOrders);
        buttonGoToOrders.setOnClickListener(v -> {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

    }
}