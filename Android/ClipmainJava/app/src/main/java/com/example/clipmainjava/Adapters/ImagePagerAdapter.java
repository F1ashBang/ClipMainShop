package com.example.clipmainjava.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clipmainjava.Models.ImageModel;
import com.example.clipmainjava.R;

import java.util.List;

public class ImagePagerAdapter extends RecyclerView.Adapter<ImagePagerAdapter.ViewHolder> {

    private List<ImageModel> images;

    public ImagePagerAdapter(List<ImageModel> images)
    {
        this.images = images;
    }
    @NonNull
    @Override
    public ImagePagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pager_image, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImagePagerAdapter.ViewHolder holder, int position) {
        ImageModel image = images.get(position);
        String url = "http://192.168.3.64:8080/images/" + image.getFileName();

        Glide.with(holder.itemImage.getContext())
                .load(url)
                .fitCenter()
                .placeholder(R.drawable.logostock)
                .error(R.drawable.ic_error)
                .into(holder.itemImage);
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView itemImage;
        public ViewHolder(View view)
        {
            super(view);
            itemImage = view.findViewById(R.id.imageViewItemInPager);
        }
    }
}
