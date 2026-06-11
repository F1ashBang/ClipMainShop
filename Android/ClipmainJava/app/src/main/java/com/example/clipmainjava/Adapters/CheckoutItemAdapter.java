package com.example.clipmainjava.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clipmainjava.Models.CartItem;
import com.example.clipmainjava.R;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutItemAdapter extends RecyclerView.Adapter<CheckoutItemAdapter.ViewHolder> {

    private final List<CartItem> items;

    public CheckoutItemAdapter(List<CartItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public CheckoutItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CheckoutItemAdapter.ViewHolder holder, int position) {
        CartItem item = items.get(position);

        String imageUrl = item.getProduct().getFirstImageURL();
        if (imageUrl != null) {
            Glide.with(holder.imageView.getContext())
                    .load(imageUrl)
                    .centerCrop()
                    .into(holder.imageView);
        }

        holder.textViewTitle.setText(item.getProduct().getTitle());
        holder.textViewPrice.setText(formatPrice(item.getProduct().getPrice()));

        String details = "Кол-во: " + item.getQuantity();
        if (item.getSize() != null) {
            details += " | размер: " + item.getSize();
        }

        holder.textViewDetails.setText(details);
        holder.textViewTotal.setText(formatPrice(item.getTotalPrice()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatPrice(Object price) {
        try {
            double value = Double.parseDouble(price.toString());
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("ru"));
            return formatter.format(value) + " ₽";
        } catch (NumberFormatException e) {
            return price.toString() + " ₽";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle, textViewPrice, textViewDetails, textViewTotal;
        public ViewHolder(@NonNull View view) {
            super(view);
            imageView = view.findViewById(R.id.imageViewCheckoutItem);
            textViewTitle = view.findViewById(R.id.textViewCheckoutTitle);
            textViewPrice = view.findViewById(R.id.textViewCheckoutPrice);
            textViewDetails = view.findViewById(R.id.textViewCheckoutDetails);
            textViewTotal = view.findViewById(R.id.textViewCheckoutTotal);
        }
    }
}
