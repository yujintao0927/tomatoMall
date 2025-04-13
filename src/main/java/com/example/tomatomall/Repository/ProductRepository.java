package com.example.tomatomall.Repository;

import com.example.tomatomall.po.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    @Query("SELECT p FROM Product p LEFT JOIN FETCH p.specifications WHERE p.id = :id")
    Optional<Product> findByIdWithSpecifications(Integer id);
}
