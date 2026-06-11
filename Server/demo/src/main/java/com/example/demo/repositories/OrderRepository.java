package com.example.demo.repositories;

import com.example.demo.models.OrderModel;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {

    List<OrderModel> findByUserOrderByCreatedAtDesc(User user);

    List<OrderModel> findByStatusOrderByCreatedAtDesc(String status);

    List<OrderModel> findByUserAndStatusOrderByCreatedAtDesc(User user, String status);
}
