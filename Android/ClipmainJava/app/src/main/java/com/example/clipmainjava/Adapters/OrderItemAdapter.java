package com.example.clipmainjava.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clipmainjava.Models.OrderItemModel;
import com.example.clipmainjava.Models.OrderModel;
import com.example.clipmainjava.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

    private final List<OrderItemModel> items;

    public OrderItemAdapter(List<OrderItemModel> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public OrderItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemAdapter.ViewHolder holder, int position) {
        OrderItemModel item = items.get(position);

        if (item.getImageUrl() != null) {
            Glide.with(holder.imageViewItem.getContext())
                    .load(item.getImageUrl())
                    .centerCrop()
                    .into(holder.imageViewItem);
        }

        holder.textViewItemTitle.setText(item.getProductTitle());
        holder.textViewItemPrice.setText(formatPrice(item.getProductPrice()));

        String details = "Кол-во: " + item.getQuantity();
        if (item.getSize() != null) {
            details += " | Размер: " + item.getSize();
        }
        holder.textViewItemDetails.setText(details);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatPrice(String price) {
        try {
            double value = Double.parseDouble(price);
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("ru"));
            return formatter.format(value) + " ₽";
        } catch (NumberFormatException e) {
            return price + " ₽";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imageViewItem;
        TextView textViewItemTitle, textViewItemPrice, textViewItemDetails;
        public ViewHolder(View view) {
            super(view);
            imageViewItem = view.findViewById(R.id.imageViewDetailItemInDetails);
            textViewItemTitle = view.findViewById(R.id.textViewDetailItemTitle);
            textViewItemPrice = view.findViewById(R.id.textViewDetailItemPrice);
            textViewItemDetails = view.findViewById(R.id.textViewDetailItemInfo);
        }
    }
}
