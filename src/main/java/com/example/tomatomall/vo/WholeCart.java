package com.example.tomatomall.vo;

import com.example.tomatomall.po.Cart;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Data
public class WholeCart {
    private List<Cart> carts;
    private Integer total;
    private BigDecimal totalAmount;


    public void setTotalAmount(){
        if(carts == null){
            return;
        }
        for (Cart cart : carts){
            totalAmount = totalAmount.add(cart.getProduct().getPrice());
        }
    }

    public void setTotal(){
        if(carts == null){
            return;
        }
        total = carts.size();
    }
}
