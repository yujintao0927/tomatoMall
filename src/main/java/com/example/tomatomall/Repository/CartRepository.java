package com.example.tomatomall.Repository;

import com.example.tomatomall.po.Account;
import com.example.tomatomall.po.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByAccount(Account account);
}
