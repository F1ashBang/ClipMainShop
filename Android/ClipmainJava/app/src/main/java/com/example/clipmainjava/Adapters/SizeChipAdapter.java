package com.example.clipmainjava.Adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clipmainjava.Models.SizeModel;
import com.example.clipmainjava.R;

import java.util.ArrayList;
import java.util.List;

public class SizeChipAdapter extends RecyclerView.Adapter<SizeChipAdapter.ViewHolder> {

    private List<SizeModel> sizes;
    private int selectedPosition = -1;
    private OnSizeSelectedListener listener;

    public interface OnSizeSelectedListener {
        void onSizeSelected(SizeModel size, int position);
    }

    public SizeChipAdapter(List<SizeModel> sizes, OnSizeSelectedListener listener) {
        this.sizes = sizes != null ? sizes : new ArrayList<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_size_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        SizeModel size = sizes.get(position);
        holder.textViewSize.setText(size.getName());

        holder.textViewSize.setSelected(position == selectedPosition);

        if (position == selectedPosition) {
            holder.textViewSize.setTextColor(holder.itemView
                    .getContext()
                    .getResources()
                    .getColor(android.R.color.white));
        }
        else {
            holder.textViewSize.setTextColor(holder.itemView
                    .getContext()
                    .getResources()
                    .getColor(android.R.color.black));
        }

        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onSizeSelected(size, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sizes != null ? sizes.size() : 0;
    }

    public SizeModel getSelectedSize() {
        if (selectedPosition >= 0 && selectedPosition < sizes.size()) {
            return sizes.get(selectedPosition);
        }
        return null;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewSize;

        public ViewHolder(View view) {
            super(view);
            textViewSize = view.findViewById(R.id.textViewSizeChip);
        }
    }
}
