package com.example.tomatomall.Repository;


import com.example.tomatomall.po.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<Orders, Integer> {
}
