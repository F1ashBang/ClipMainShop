package com.example.clipmainjava.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clipmainjava.Models.ImageModel;
import com.example.clipmainjava.R;

import java.util.List;
import androidx.recyclerview.widget.RecyclerView;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {
    public List<ImageModel> images;

    public ImageAdapter(List<ImageModel> images) { this.images = images; }


    @NonNull
    @Override
    public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item, parent, false);
        return new ViewHolder(view);    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter.ViewHolder holder, int position) {
        ImageModel image = images.get(position);

        Glide.with(holder.imageView.getContext())
                .load(image.getImageURL())
                .placeholder(R.drawable.logostock)
                .error(R.drawable.logo)
                .into(holder.imageView);

        holder.textViewTitle.setText(image.getUploadedAt());
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textViewTitle, textViewPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageViewShopItem);
            textViewTitle = itemView.findViewById(R.id.textViewItemTitle);
            textViewPrice = itemView.findViewById(R.id.textViewItemPrice);
        }
    }
}
