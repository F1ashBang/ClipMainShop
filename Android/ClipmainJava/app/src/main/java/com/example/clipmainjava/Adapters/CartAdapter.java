package com.example.clipmainjava.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private final List<CartItem> items;
    private final CartListener listener;

    public interface CartListener {
        void onQuantityChanged(Long productId, int newQuantity);
        void onItemRemoved(Long productId);
    }

    public CartAdapter(List<CartItem> items, CartListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CartItem item = items.get(position);

        String imageUrl = item.getProduct().getFirstImageURL();
        if (imageUrl != null) {
            Glide.with(holder.itemView.getContext())
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_placeholder)
                    .error(R.drawable.logostock)
                    .fitCenter()
                    .into(holder.imageViewItem);
        }

        holder.textViewTitle.setText(item.getProduct().getTitle());

        holder.textViewPrice.setText(formatPrice(item.getProduct().getPrice()));

        holder.textViewQuantity.setText(String.valueOf(item.getQuantity()));

        holder.textViewPrice.setText(formatPrice(item.getTotalPrice()));

        if (item.getSize() != null && !item.getSize().isEmpty()) {
            holder.textViewSize.setText(item.getSize());
            holder.textViewSize.setVisibility(View.VISIBLE);
        }
        else {
            holder.textViewSize.setText("Размер не выбран");
            holder.textViewSize.setVisibility(View.VISIBLE);
        }

        holder.imageButtonPlus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            listener.onQuantityChanged(item.getProduct().getId(), newQuantity);
        });

        holder.imageButtonMinus.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() - 1;
            if (newQuantity <= 0) {
                listener.onItemRemoved(item.getProduct().getId());
            }
            else {
                listener.onQuantityChanged(item.getProduct().getId(), newQuantity);
            }
        });

        holder.imageButtonTrash.setOnClickListener(v -> {
            listener.onItemRemoved(item.getProduct().getId());
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatPrice(Object price) {
        try {
            double value;

            if (price instanceof String) {
                value = Double.parseDouble((String) price);
            }
            else if (price instanceof Double) {
                value = (Double) price;
            }
            else {
                return price.toString()+ " ₽";
            }
            NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("ru"));
            return  formatter.format(value) + " ₽";
        }
        catch (NumberFormatException e) {
            return price.toString() + " ₽";
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewTitle, textViewSize, textViewPrice, textViewQuantity;
        ImageView imageViewItem;
        ImageButton imageButtonPlus, imageButtonMinus, imageButtonTrash;

        public ViewHolder(@NonNull View view) {
            super(view);
            textViewTitle = view.findViewById(R.id.textViewItemTitleInCartItem);
            textViewSize = view.findViewById(R.id.textViewSizeInCartItem);
            textViewPrice = view.findViewById(R.id.textViewPriceItemInCartItem);
            textViewQuantity = view.findViewById(R.id.textViewQuantityInCartItem);
            imageViewItem = view.findViewById(R.id.imageViewItemInCartItem);
            imageButtonPlus = view.findViewById(R.id.imageButtonItemPlusInCartItem);
            imageButtonMinus = view.findViewById(R.id.imageButtonItemMinusInCartItem);
            imageButtonTrash = view.findViewById(R.id.imageButtonTrashInCartItem);
        }
    }
}
