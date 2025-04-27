package com.example.tomatomall.Repository;

import com.example.tomatomall.po.Stockpile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockpileRepository extends JpaRepository<Stockpile, Integer> {
}
