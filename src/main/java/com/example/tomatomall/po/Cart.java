package com.example.tomatomall.po;


import com.example.tomatomall.vo.CartVO;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartItemId")
    private int cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(columnDefinition = "INT DEFAULT 1")
    private Integer quantity = 1;

    public CartVO toVO(){
        CartVO cartVO = new CartVO();
        cartVO.setCartItemId(this.cartItemId);
        cartVO.setDescription(this.product.getDescription());
        cartVO.setPrice(this.product.getPrice());
        cartVO.setQuantity(this.quantity);
        cartVO.setProductId(this.product.getId());
        cartVO.setDetail(this.product.getDetail());
        cartVO.setTitle(this.product.getTitle());
        cartVO.setCover(this.product.getCover());
        return cartVO;
    }
}
