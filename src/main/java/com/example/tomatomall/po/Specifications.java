package com.example.tomatomall.po;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name = "specifications")
@Data
public class Specifications {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "item", nullable = false, length = 50)
    private String item;

    @Column(name = "value", nullable = false, length = 255)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}