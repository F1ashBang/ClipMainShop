package com.example.clipmainjava;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clipmainjava.Adapters.CheckoutItemAdapter;
import com.example.clipmainjava.CartRealize.CartManager;
import com.example.clipmainjava.Database.ApiClient;
import com.example.clipmainjava.Database.ApiService;
import com.example.clipmainjava.Models.CartItem;
import com.example.clipmainjava.Models.DaDataResponse;
import com.example.clipmainjava.Models.DaDataSuggestion;
import com.example.clipmainjava.Models.LastOrderModel;
import com.example.clipmainjava.Session.UserSession;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextMiddleName, editTextPhone, editTextAddress;
    private TextView textViewTotalPrice;
    private Button buttonPlaceOrder;
    private RecyclerView recyclerView;
    private CheckoutItemAdapter adapter;
    private CartManager cartManager;

    private boolean isAddressSelected = false;
    private Handler handler = new Handler();
    private Runnable suggestRunnable;
    private static final String DADATA_API_KEY = "Token 1e8571b0248de1b9a5f838c684597c65383b6c14";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_checkout);

        editTextFirstName = findViewById(R.id.editTextFirstNameInOrder);
        editTextLastName = findViewById(R.id.editTextLastNameInOrder);
        editTextMiddleName = findViewById(R.id.editTextMiddleNameInOrder);
        editTextAddress = findViewById(R.id.editTextAddressInOrder);
        editTextPhone = findViewById(R.id.editTextPhoneInOrder);
        textViewTotalPrice = findViewById(R.id.textViewCheckoutTotalInOrder);

        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrderInOrder);

        recyclerView = findViewById(R.id.recyclerViewCheckoutItems);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cartManager = CartManager.getInstance(this);

        editTextAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Log.d("DADATA_DEBUG", "Введено: '" + s.toString() + "', длина: " + s.length());
                if (isAddressSelected) {
                    isAddressSelected = false;
                    return;
                }

                if (suggestRunnable != null) {
                    handler.removeCallbacks(suggestRunnable);
                }

                if (s.length() > 3) {
                    suggestRunnable = new Runnable() {
                        @Override
                        public void run() {
                            suggestAddress(s.toString());
                        }
                    };

                    handler.postDelayed(suggestRunnable, 1000);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        loadOrderSummary();
        loadLastOrderData();
    }

    private void loadOrderSummary() {
        List<CartItem> items = cartManager.getItems();

        if (items.isEmpty()) {
            Toast.makeText(this, "Корзина пуста", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        adapter = new CheckoutItemAdapter(items);
        recyclerView.setAdapter(adapter);
        double total = cartManager.getTotalPrice();
        textViewTotalPrice.setText(formatPrice(total));
    }

    public void Click_buttonPlaceHolder(View view) {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String middleName = editTextMiddleName.getText().toString().trim();
        String phone = editTextPhone.getText().toString().trim();
        String address = editTextAddress.getText().toString().trim();

        if (firstName.isEmpty()) {
            Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show();
            return;
        }
        if (lastName.isEmpty()) {
            Toast.makeText(this, "Введите фамилию", Toast.LENGTH_SHORT).show();
            return;
        }
        if (address.isEmpty()) {
            Toast.makeText(this, "Введите адрес", Toast.LENGTH_SHORT).show();
            return;
        }

        buttonPlaceOrder.setEnabled(false);
        buttonPlaceOrder.setText("Оформление...");

        createOrder(firstName, lastName, middleName, phone, address);
    }

    public void createOrder(String firstName, String lastName,
                            String middleName, String phone, String address) {
        ApiService service = ApiClient.getService();

        Map<String, Object> body = new HashMap<>();

        long userId = UserSession.getInstance(this).getUserId();
        if (userId == -1) {
            Toast.makeText(this, "Необходимо войти", Toast.LENGTH_SHORT).show();
            return;
        }

        body.put("userId", userId);
        body.put("firstName", firstName);
        body.put("lastName", lastName);
        body.put("middleName", middleName);
        body.put("phone", phone);
        body.put("address", address);

        List<Map<String, Object>> items = new ArrayList<>();
        for (CartItem item : cartManager.getItems()) {
            Map<String, Object> itemData = new HashMap<>();
            itemData.put("productId", item.getProduct().getId());
            itemData.put("productTitle", item.getProduct().getTitle());
            itemData.put("productPrice", item.getProduct().getPrice());
            itemData.put("size", item.getSize());
            itemData.put("quantity", item.getQuantity());
            itemData.put("imageUrl", item.getProduct().getFirstImageURL());
            items.add(itemData);
        }
        body.put("items", items);

        service.createOrder(body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                buttonPlaceOrder.setEnabled(true);
                buttonPlaceOrder.setText("Оформить заказ");

                if (response.isSuccessful() && response.body() != null) {
                    cartManager.clear();

                    Object orderIdObj = response.body().get("orderId");
                    String orderId = orderIdObj != null ? String.valueOf(Double.valueOf(orderIdObj.toString()).longValue()) : "?";
                    Toast.makeText(CheckoutActivity.this,
                            "Заказ №" + orderId + " оформлен!", Toast.LENGTH_LONG).show();

                    Intent intent = new Intent(CheckoutActivity.this, OrderSuccessActivity.class);
                    intent.putExtra("orderId", orderId != null ? orderId.toString() : "");
                    startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(CheckoutActivity.this,
                            "Ошибка оформления заказа", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                buttonPlaceOrder.setEnabled(true);
                buttonPlaceOrder.setText("Оформить заказ");

                Toast.makeText(CheckoutActivity.this,
                        "Сервер недоступен: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("ru"));
        return formatter.format(price) + " ₽";
    }

    private void loadLastOrderData() {
        long userId = UserSession.getInstance(this).getUserId();
        if (userId == -1) return;

        String phone = UserSession.getInstance(this).getPhone();
        if (phone != null && !phone.isEmpty()) {
            editTextPhone.setText(phone);
        }

        ApiService service = ApiClient.getService();
        service.getLastUserOrder(userId).enqueue(new Callback<LastOrderModel>() {
            @Override
            public void onResponse(Call<LastOrderModel> call, Response<LastOrderModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    LastOrderModel lastOrder = response.body();

                    if (lastOrder.isHasOrder()) {
                        // Заполняем поля из последнего заказа
                        if (lastOrder.getFirstName() != null && !lastOrder.getFirstName().isEmpty()) {
                            editTextFirstName.setText(lastOrder.getFirstName());
                        }
                        if (lastOrder.getLastName() != null && !lastOrder.getLastName().isEmpty()) {
                            editTextLastName.setText(lastOrder.getLastName());
                        }
                        if (lastOrder.getMiddleName() != null && !lastOrder.getMiddleName().isEmpty()) {
                            editTextMiddleName.setText(lastOrder.getMiddleName());
                        }
                        if (lastOrder.getAddress() != null && !lastOrder.getAddress().isEmpty()) {
                            editTextAddress.setText(lastOrder.getAddress());
                        }
                        // Телефон из последнего заказа (приоритетнее, чем из сессии)
                        if (lastOrder.getPhone() != null && !lastOrder.getPhone().isEmpty()) {
                            editTextPhone.setText(lastOrder.getPhone());
                        }
                        else {
                            editTextPhone.setText(UserSession.getInstance(CheckoutActivity.this).getPhone());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LastOrderModel> call, Throwable t) {

            }
        });
    }

    private void suggestAddress(String query) {
        Log.d("DADATA_DEBUG", "Запрос адреса: " + query);

        ApiClient.getDaDataService().suggestAddress(DADATA_API_KEY, query, 5)
                .enqueue(new Callback<DaDataResponse>() {
                    @Override
                    public void onResponse(Call<DaDataResponse> call, Response<DaDataResponse> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().suggestions != null) {
                            List<String> suggestions = new ArrayList<>();
                            for (DaDataSuggestion s : response.body().suggestions) {
                                suggestions.add(s.value);
                            }
                            showAddressSuggestions(suggestions);
                        }
                    }

                    @Override
                    public void onFailure(Call<DaDataResponse> call, Throwable t) {

                    }
                });
    }

    private void showAddressSuggestions(List<String> suggestions) {
        if (suggestions.isEmpty()) return;

        String[] items = suggestions.toArray(new String[0]);
        new AlertDialog.Builder(CheckoutActivity.this)
                .setTitle("Выберите адресс")
                .setItems(items, (dialog, which) -> {
                    isAddressSelected = true;
                   editTextAddress.setText(items[which]);
                })
                .show();
    }

}
