package com.example.clipmainjava;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clipmainjava.Adapters.OrderAdapter;
import com.example.clipmainjava.Database.ApiClient;
import com.example.clipmainjava.Database.ApiService;
import com.example.clipmainjava.Models.OrderModel;
import com.example.clipmainjava.Session.UserSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private OrderAdapter adapter;
    private String filterType;

    private static final List<String> ACTIVE_STATUSES = Arrays.asList("new", "processing", "shipped");
    private static final List<String> COMPLETED_STATUSES = Arrays.asList("delivered", "cancelled");


    public static OrdersFragment newInstance(String filterType) {
        OrdersFragment fragment = new OrdersFragment();
        Bundle args = new Bundle();
        args.putString("filterType", filterType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            filterType = getArguments().getString("filterType");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container,
                             @NonNull Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        recyclerView = view.findViewById(R.id.recyclerViewOrders);
        textViewEmpty = view.findViewById(R.id.textViewEmpty);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadOrders();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        loadOrders();
    }

    private void loadOrders() {
        long userId = UserSession.getInstance(requireContext()).getUserId();
        if (userId == -1) {
            showEmpty(true);
            return;
        }

        ApiService service = ApiClient.getService();

        service.getUserOrders(userId).enqueue(new Callback<List<OrderModel>>() {
            @Override
            public void onResponse(Call<List<OrderModel>> call, Response<List<OrderModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<OrderModel> allOrders = response.body();
                    List<OrderModel> filtered = new ArrayList<>();

                    for (OrderModel order : allOrders) {
                        String status = order.getStatus();
                        if ("active".equals(filterType)) {
                            if (status != null && ACTIVE_STATUSES.contains(status)) {
                                filtered.add(order);
                            }
                        }
                        else {
                            if (status != null && COMPLETED_STATUSES.contains(status)) {
                                filtered.add(order);
                            }
                        }
                    }

                    if (filtered.isEmpty()) {
                        showEmpty(true);
                    }
                    else {
                        showEmpty(false);
                        adapter = new OrderAdapter(filtered, order -> {
                            Intent intent = new Intent(getContext(), OrderDetailActivity.class);
                            intent.putExtra("orderId", order.getId());
                            startActivity(intent);
                        });
                        recyclerView.setAdapter(adapter);
                    }
                }
                else {
                    showEmpty(true);
                }
            }

            @Override
            public void onFailure(Call<List<OrderModel>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка загрузки", Toast.LENGTH_SHORT).show();
                showEmpty(true);
            }
        });

    }

    private void showEmpty(boolean empty) {
        if (empty) {
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
        }
        else {
            recyclerView.setVisibility(View.VISIBLE);
            textViewEmpty.setVisibility(View.GONE);
        }
    }
}