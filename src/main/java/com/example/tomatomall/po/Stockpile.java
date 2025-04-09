package com.example.tomatomall.po;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "stockpiles")
@Data
public class Stockpile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "amount", nullable = false)
    private Integer amount;

    @Column(name = "frozen", nullable = false)
    private Integer frozen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    @JsonBackReference // ✅ 解决无限递归
    private Product product;
}