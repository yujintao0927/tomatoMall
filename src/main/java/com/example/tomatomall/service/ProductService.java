package com.example.tomatomall.service;

import com.example.tomatomall.po.Product;

import java.util.List;

public interface ProductService {
    Product createProduct(Product product);
    Product getProductById(Integer id);
    List<Product> getAllProducts();
    Product updateProduct(Integer id, Product updatedProduct);
    void deleteProduct(Integer id);
}
