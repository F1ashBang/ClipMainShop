package com.example.clipmainjava.Adapters;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class StaticHeaderAdapter extends RecyclerView.Adapter<StaticHeaderAdapter.ViewHolder> {

    private final View view;

    public StaticHeaderAdapter(View view) {
        this.view = view;
    }

    @NonNull
    @Override
    public StaticHeaderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StaticHeaderAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
