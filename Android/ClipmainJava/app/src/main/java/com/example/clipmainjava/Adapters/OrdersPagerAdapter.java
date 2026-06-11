package com.example.clipmainjava.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clipmainjava.OrdersFragment;

public class OrdersPagerAdapter extends FragmentStateAdapter {

    public OrdersPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return OrdersFragment.newInstance("active");
            case 1:
                return OrdersFragment.newInstance("completed");
            default:
                return OrdersFragment.newInstance("active");
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }


}
