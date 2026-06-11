package com.example.clipmainjava;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.clipmainjava.Adapters.OrdersPagerAdapter;
import com.example.clipmainjava.Session.UserSession;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class ProfileActivity extends AppCompatActivity {

    private TextView textViewPhone, textViewUserId;
    private Button buttonLogout;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        textViewPhone = findViewById(R.id.textViewProfilePhone);
        textViewUserId = findViewById(R.id.textViewProfileId);

        tabLayout = findViewById(R.id.tabLayoutProfile);
        viewPager = findViewById(R.id.viewPagerProfile);

        UserSession session = UserSession.getInstance(this);

        if (!session.isLoggedIn()) {
            Toast.makeText(this, "Вы не вошли", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        textViewPhone.setText(session.getPhone());
        textViewUserId.setText("ID: " + session.getUserId());

        buttonLogout = findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(v -> {
            session.logout();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        setupOrderTabs();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(0, 0, 0, systemBars.bottom);
            return insets;
        });
    }

    private void setupOrderTabs() {
        OrdersPagerAdapter adapter = new OrdersPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText("📋 Текущие");
                    break;
                case 1:
                    tab.setText("📦 История");
                    break;
            }
        }).attach();
    }

    public void Click_buttonGoToBasketFromProfile(View view) {
        Intent intent = new Intent(ProfileActivity.this, CartActivity.class);
        startActivity(intent);
    }

    public void Click_buttonGoToMainFromProfile(View view) {
        Intent intent = new Intent(ProfileActivity.this, MainShopActivity.class);
        startActivity(intent);
    }

    public void Click_buttonGoToProfileFromProfile(View view) {
    }
}