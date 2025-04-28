package com.example.tomatomall.Repository;

import com.example.tomatomall.po.Cart;
import com.example.tomatomall.po.CartsOrdersRelation;
import com.example.tomatomall.po.Orders;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CartsOrdersRelationRepository extends CrudRepository<CartsOrdersRelation, Integer> {
    List<Cart> findCartsOrdersRelationByOrders(Orders orders);
}
