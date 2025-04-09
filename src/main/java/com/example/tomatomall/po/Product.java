package com.example.tomatomall.po;

import com.example.tomatomall.po.Stockpile;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "title", nullable = false, length = 50)
    private String title;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "rate", nullable = false)
    private Double rate;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "cover", length = 500)
    private String cover;

    @Column(name = "detail", length = 500)
    private String detail;

    // 一对多关联规格表
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Specifications> specifications;

    // 一对一关联库存表
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // ✅ 解决无限递归
    private Stockpile stockpile;

    // 辅助方法：添加规格
    public void addSpecification(Specifications specification) {
        this.specifications.add(specification);
        if(specification != null){
            specification.setProduct(this);
        }

    }

    // 辅助方法：设置库存
    public void setStockpile(Stockpile stockpile) {
        this.stockpile = stockpile;
        if(stockpile != null){
            stockpile.setProduct(this);
        }

    }
}