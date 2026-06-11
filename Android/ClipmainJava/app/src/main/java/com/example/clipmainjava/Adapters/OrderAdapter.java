package com.example.clipmainjava.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clipmainjava.Models.OrderModel;
import com.example.clipmainjava.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private final List<OrderModel> orders;
    private final OnOrderClickListener listener;

    public interface OnOrderClickListener {
        void onOrderClick(OrderModel order);
    }

    public OrderAdapter(List<OrderModel> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        OrderModel order = orders.get(position);

        holder.textViewOrderId.setText("Заказ №" + order.getId());
        holder.textViewStatus.setText(getStatusText(order.getStatus()));
        holder.textViewStatus.setTextColor(getStatusColor(order.getStatus()));
        holder.textViewItemsCount.setText(getItemsText(order.getItemsCount()));
        holder.textViewTotal.setText(formatPrice(order.getTotalPrice()));
        holder.textViewOrderDate.setText(order.getCreatedAt());

        holder.itemView.setOnClickListener(v -> listener.onOrderClick(order));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    private String getStatusText(String status) {
        if (status == null) return "-";
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

    private String getItemsText(int count) {
        int lastDigit = count % 10;
        int lastTwo = count % 100;
        if (lastTwo >= 11 && lastTwo <= 19) return count + " товаров";
        if (lastDigit == 1) return count + " товар";
        if (lastDigit >= 2 && lastDigit <= 4) return count + " товара";
        return count + " товаров";
    }

    private String formatPrice(double price) {
        NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("ru"));
        return formatter.format(price) + " ₽";
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewOrderId, textViewStatus, textViewItemsCount, textViewTotal, textViewOrderDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderIdInOrder);
            textViewStatus = itemView.findViewById(R.id.textViewStatusInOrder);
            textViewItemsCount = itemView.findViewById(R.id.textViewItemsCountInOrder);
            textViewTotal = itemView.findViewById(R.id.textViewOrderTotalInOrder);
            textViewOrderDate = itemView.findViewById(R.id.textViewOrderDateInOrder);
        }
    }
}
