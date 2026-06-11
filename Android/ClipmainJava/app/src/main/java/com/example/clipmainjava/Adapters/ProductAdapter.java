package com.example.clipmainjava.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clipmainjava.ItemViewActivity;
import com.example.clipmainjava.Models.ProductModel;
import com.example.clipmainjava.R;
import com.google.gson.Gson;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private final List<ProductModel> products;

    public ProductAdapter(List<ProductModel> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ProductModel product = products.get(position);
        holder.textViewTitle.setText(product.getTitle());
        holder.textViewPrice.setText(String.format("%,.0f ₽", product.getPrice()));

        String url = product.getFirstImageURL();
        if (product.getFirstImageURL() != null) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getFirstImageURL())
                    .into(holder.imageViewProduct);

        }

        holder.imageViewProduct.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ItemViewActivity.class);
            intent.putExtra("id", product.getId());
            intent.putExtra("title", product.getTitle());
            intent.putExtra("description", product.getDescription());
            intent.putExtra("price", product.getPrice());

            Gson gson = new Gson();
            String imagesJson = gson.toJson(product.getImages());
            intent.putExtra("imagesJson", imagesJson);

            String sizesJson = gson.toJson(product.getSizes());
            intent.putExtra("sizesJson", sizesJson);

            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewProduct;
        TextView textViewPrice, textViewTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            imageViewProduct = itemView.findViewById(R.id.imageViewShopItem);
            textViewTitle = itemView.findViewById(R.id.textViewItemTitle);
            textViewPrice = itemView.findViewById(R.id.textViewItemPrice);
        }
    }
}
