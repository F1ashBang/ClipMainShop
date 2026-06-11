package com.example.clipmainjava;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.os.Bundle;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.clipmainjava.Database.ApiClient;
import com.example.clipmainjava.Database.ApiService;
import com.example.clipmainjava.Session.UserSession;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CodeActivity extends AppCompatActivity {

    private String phoneNumber;
    private EditText editTextCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_code);
        phoneNumber = getIntent().getStringExtra("phone");
        editTextCode = findViewById(R.id.editTextVerificationCode);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Click_buttonVerifyUserCode(View view) {
        String code = editTextCode.getText().toString();

        if (code.isEmpty()) {
            Toast.makeText(this, "Введите код", Toast.LENGTH_SHORT).show();
            return;
        }

        verifyCodeOnServer(phoneNumber, code);
    }

    private void verifyCodeOnServer(String phone, String code) {
        if (phone == null || phone.isEmpty()) {
            Toast.makeText(this, "Ошибка: номер телефона не передан", Toast.LENGTH_LONG).show();
            return;
        }

        ApiService service = ApiClient.getService();

        Map<String, String> body = new HashMap<>();
        body.put("phone", phone);
        body.put("code", code);

        service.verifyCode(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    if (response.isSuccessful()) {
                        Object userIdObj = response.body().get("userId");
                        if (userIdObj != null) {
                            long userId = Double.valueOf(userIdObj.toString()).longValue();
                            String phone = getIntent().getStringExtra("phone");
                            UserSession.getInstance(CodeActivity.this).saveUser(userId, phone);
                        }

                        // Переход дальше
                        Intent intent = new Intent(CodeActivity.this, MainShopActivity.class);
                        startActivity(intent);
                        finish();
                    }}
                else {
                    Toast.makeText(CodeActivity.this, "Неверный код", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(CodeActivity.this, "Сервер недоступен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}