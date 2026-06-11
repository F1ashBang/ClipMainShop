package com.example.clipmainjava;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

import com.example.clipmainjava.Adapters.OrderItemAdapter;
import com.example.clipmainjava.Database.ApiClient;
import com.example.clipmainjava.Database.ApiService;
import com.example.clipmainjava.Models.OrderModel;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private TextView textViewDetailTitle, textViewDetailStatus, textViewDetailName,
            textViewDetailPhone, textViewDetailAddress, textViewDetailTotal, textViewDetailDate;
    private RecyclerView recyclerView;

    private Button buttonCancelOrder;
    private long currentOrderId;
    private String currentStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_detail);

        long orderId = getIntent().getLongExtra("orderId", -1);
        Log.d("DETAIL_DEBUG", "Получен orderId: " + orderId);

        ImageButton buttonBack = findViewById(R.id.imageButtonBackFromDetail);
        buttonBack.setOnClickListener(v -> finish());

        textViewDetailTitle = findViewById(R.id.textViewDetailTitleInOrderDetails);
        textViewDetailStatus = findViewById(R.id.textViewDetailStatusInOrderDetails);
        textViewDetailName = findViewById(R.id.textViewDetailNameInOrderDetails);
        textViewDetailPhone = findViewById(R.id.textViewDetailPhoneInOrderDetails);
        textViewDetailAddress = findViewById(R.id.textViewDetailAddressInOrderDetails);
        textViewDetailTotal = findViewById(R.id.textViewDetailTotalInOrderDetails);
        textViewDetailDate = findViewById(R.id.textViewDetailDateInOrderDetails);
        recyclerView = findViewById(R.id.recyclerViewDetailItemsInOrderDetails);
        buttonCancelOrder = findViewById(R.id.buttonCancelOrder);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (orderId == -1 ) {
            Toast.makeText(this, "Заказ не найден", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadOrderDetails(orderId);
    }

    private void loadOrderDetails(long orderId) {
        ApiService service = ApiClient.getService();
        service.getOrder(orderId).enqueue(new Callback<OrderModel>() {
            @Override
            public void onResponse(Call<OrderModel> call, Response<OrderModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    OrderModel order = response.body();
                    fillData(order);
                }
                else {
                    Toast.makeText(OrderDetailActivity.this, "Ошибка загрузки заказа", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<OrderModel> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Сервер недоступен", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fillData(OrderModel order) {
        currentStatus = order.getStatus();
        currentOrderId = order.getId();
        textViewDetailTitle.setText("Заказ №" + order.getId());

        textViewDetailStatus.setText(getStatusText(order.getStatus()));
        textViewDetailStatus.setTextColor(getStatusColor(order.getStatus()));

        String name = (order.getLastName() != null ? order.getLastName() : "") + " " +
                (order.getFirstName() != null ? order.getFirstName() : "");
        if (order.getPhoneAtOrder() != null && !order.getPhoneAtOrder().isEmpty()) {
            name = name.trim();
        }

        textViewDetailName.setText(name.trim());
        textViewDetailPhone.setText(order.getPhoneAtOrder() != null ? order.getPhoneAtOrder() : "-");

        textViewDetailAddress.setText(order.getAddress() != null ? order.getAddress() : "-");

        if (order.getItems() != null && !order.getItems().isEmpty()) {
            OrderItemAdapter adapter = new OrderItemAdapter(order.getItems());
            recyclerView.setAdapter(adapter);
        }

        textViewDetailTotal.setText(formatPrice(order.getTotalPrice()));
        textViewDetailDate.setText("Заказ создан: " + order.getCreatedAt());

        if ("new".equals(currentStatus) || "processing".equals(currentStatus)) {
            buttonCancelOrder.setVisibility(View.VISIBLE);
        } else {
            buttonCancelOrder.setVisibility(View.GONE);
        }
    }

    private String getStatusText(String status) {
        if (status == null) return "—";
        switch (status) {
            case "new": return "🆕 Новый";
            case "processing": return "⚙️ В обработке";
            case "shipped": return "🚚 В пути";
            case "delivered": return "✅ Доставлен";
            case "cancelled": return "❌ Отменён";
            default: return status;
        }
    }

    private int getStatusColor(String status) {
        if (status == null) return 0xFF888888;
        switch (status) {
            case "new": return 0xFF2196F3;
            case "processing": return 0xFFFF9800;
            case "shipped": return 0xFF9C27B0;
            case "delivered": return 0xFF4CAF50;
            case "cancelled": return 0xFFF44336;
            default: return 0xFF888888;
        }
    }

    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("ru"));
        return formatter.format(price) + " ₽";
    }

    public void Click_cancelOrder(View view) {
        showCancelDialog(currentOrderId);
    }

    private void showCancelDialog(long orderId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_cancel_order, null);
        builder.setView(dialogView);

        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupReasons);
        EditText editTextOther = dialogView.findViewById(R.id.editTextOtherReason);

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioOther) {
                editTextOther.setVisibility(View.VISIBLE);
            }
            else {
                editTextOther.setVisibility(View.GONE);
            }
        });

        builder.setPositiveButton("Отменить заказ", (dialog, which) ->{
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Выберите", Toast.LENGTH_SHORT).show();
                return;
            }

            String reason;
            if (selectedId == R.id.radioOther) {
                reason = editTextOther.getText().toString().trim();
                if (reason.isEmpty()) {
                    Toast.makeText(this, "Напишите причину", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else {
                RadioButton radioButton = dialogView.findViewById(selectedId);
                reason = radioButton.getText().toString();
            }

            cancelReason(orderId, reason);
        });

        builder.setNegativeButton("Закрыть", null);
        builder.show();
    }

    private void cancelReason(long orderId, String reason) {
        ApiService service = ApiClient.getService();

        Map<String, String> body = new HashMap<>();
        body.put("status", "cancelled");
        body.put("cancellationReason", reason);

        service.updateOrderStatus(orderId, body).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(OrderDetailActivity.this, "Заказ отменён", Toast.LENGTH_SHORT).show();

                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("orderCancelled", true);
                    setResult(RESULT_OK, resultIntent);
                    finish();
                } else {
                    Toast.makeText(OrderDetailActivity.this, "Ошибка отмены", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(OrderDetailActivity.this, "Сервер недоступен", Toast.LENGTH_SHORT).show();
            }
        });
    }
}