package com.example.clipmainjava;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void Click_buttonSignUp(View view) {

        EditText editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        String phone = editTextPhoneNumber.getText().toString();

        ApiService service = ApiClient.getService();

        service.checkPhone(phone).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Map<String, Object> result = response.body();
                    Boolean valid = (Boolean) result.get("valid");
                    Boolean exists = (Boolean) result.get("exists");

                    if (valid && !exists) {
                        registerPhoneOnServer(phone);
                    }
                    else if (!valid) {
                        Toast.makeText(MainActivity.this, "Номер должен содержать 11 цифр!", Toast.LENGTH_SHORT).show();
                    }
                    else if (exists) {
                        Toast.makeText(MainActivity.this, "Номер уже зарегистрирован!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void registerPhoneOnServer(String phone) {
        ApiService service = ApiClient.getService();

        Map<String, String> body = new HashMap<>();
        body.put("phone", phone);

        service.registerPhone(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Object userIdObj = response.body().get("userId");
                    long userId;
                    if (userIdObj instanceof Double) {
                        userId = ((Double) userIdObj).longValue();
                    } else if (userIdObj instanceof Integer) {
                        userId = ((Integer) userIdObj).longValue();
                    } else {
                        userId = Long.parseLong(userIdObj.toString());
                    }
                    Toast.makeText(MainActivity.this, "Номер успешно зарегистрирован!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Ошибка регистрации", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Сервер недоступен", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void Click_buttonLogIn(View view) {
        EditText editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        String phone = editTextPhoneNumber.getText().toString();

        if (!phone.matches("\\d{11}")) {
            Toast.makeText(MainActivity.this,
                    "Номер должен содержать 11 цифр",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        logInPhoneOnService(phone);
    }

    private void logInPhoneOnService(String phone) {
        ApiService service = ApiClient.getService();

        Map<String, String> body = new HashMap<>();
        body.put("phone", phone);

        service.loginPhone(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Object userIdObj = response.body().get("userId");
                    long userId;
                    if (userIdObj instanceof Double) {
                        userId = ((Double) userIdObj).longValue();
                    } else if (userIdObj instanceof Integer) {
                        userId = ((Integer) userIdObj).longValue();
                    } else {
                        userId = Long.parseLong(userIdObj.toString());
                    }

                    Intent intent = new Intent(MainActivity.this, CodeActivity.class);
                    intent.putExtra("phone", phone);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(MainActivity.this, "Ошибка входа, проверьте номер", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Сервер недоступен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}