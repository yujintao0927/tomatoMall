package com.example.tomatomall.Repository;

import com.example.tomatomall.po.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer> {
}
