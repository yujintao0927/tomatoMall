package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.po.Product;
import com.example.tomatomall.Repository.ProductRepository;
import com.example.tomatomall.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product getProductById(Integer id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public Product updateProduct(Integer id, Product updatedProduct) {
        Optional<Product> optional = productRepository.findById(id);
        if (optional.isPresent()) {
            Product product = optional.get();
            product.setTitle(updatedProduct.getTitle());
            product.setPrice(updatedProduct.getPrice());
            product.setRate(updatedProduct.getRate());
            product.setDescription(updatedProduct.getDescription());
            product.setCover(updatedProduct.getCover());
            product.setDetail(updatedProduct.getDetail());
            if(product.getSpecifications() != null){
                product.setSpecifications(updatedProduct.getSpecifications());
            }
            if(product.getStockpile() != null){
                product.setStockpile(updatedProduct.getStockpile());
            }
            return productRepository.save(product);
        }else throw new RuntimeException("product not found, id:" + id);
        //return null;
    }

    @Override
    public void deleteProduct(Integer id) {
        productRepository.deleteById(id);
    }
}
