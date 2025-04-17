package com.example.tomatomall.service.serviceImpl;

import com.example.tomatomall.Repository.ProductRepository;
import com.example.tomatomall.exception.TomatoMallException;
import com.example.tomatomall.po.Product;
import com.example.tomatomall.po.Specifications;
import com.example.tomatomall.service.ProductService;
import com.example.tomatomall.vo.ProductVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductVO createProduct(ProductVO productVO) {
        Product product = productVO.toPO();
        productRepository.save(product);
        return productVO;
    }

    @Override
    public ProductVO getProductById(Integer id) {
        Product product = productRepository.findById(id).orElseThrow(TomatoMallException::productNotFound);
        return product.toVO();
    }

    @Override
    public List<ProductVO> getAllProducts() {
        List<Product> products = productRepository.findAll();
        List<ProductVO> productVOList = new ArrayList<>();
        for (Product product : products) {
            productVOList.add(product.toVO());
        }
        return productVOList;
    }

    @Override
    public String updateProduct(ProductVO updatedProduct) {
        Integer id = updatedProduct.getId();

        if(id == null) {
            throw TomatoMallException.productNotFound();
        }

        Product productInRepository = productRepository.findById(id).orElseThrow(TomatoMallException::productNotFound);

        updateProductBasicInfo(productInRepository, updatedProduct);

        if(updatedProduct.getSpecifications() != null) {
            List<ProductVO.SpecificationVO> specificationVOs = updatedProduct.getSpecifications();

            List<Specifications> specifications = productInRepository.getSpecifications();

            Map<Integer, Specifications> specificationsMap = specifications.stream()
                    .collect(Collectors.toMap(Specifications::getId, spec -> spec));

            for(ProductVO.SpecificationVO specificationVO : specificationVOs) {
                int specificationId = specificationVO.getId();
                if(specificationsMap.containsKey(specificationId)) {//更改
                    Specifications existingSpec = specificationsMap.get(specificationId);
                    existingSpec.setItem(specificationVO.getItem());
                    existingSpec.setValue(specificationVO.getValue());
                }else{//新增
                    specifications.add(specificationVO.toPO());
                }
            }
            productInRepository.setSpecifications(specifications);
        }

        productRepository.save(productInRepository);

//        if(updatedProduct.getId() != null) {
//            productInRepository.setId(updatedProduct.getId());
//        }
//
//        if(updatedProduct.getRate() != null) {
//            productInRepository.setId(updatedProduct.getId());
//        }

//        productInRepository.setTitle(updatedProduct.getTitle());
//        productInRepository.setPrice(updatedProduct.getPrice());
//        productInRepository.setRate(updatedProduct.getRate());
//        productInRepository.setDescription(updatedProduct.getDescription());
//        productInRepository.setCover(updatedProduct.getCover());
//        productInRepository.setDetail(updatedProduct.getDetail());
//        if (productInRepository.getSpecifications() != null) {
//            productInRepository.setSpecifications(updatedProduct.getSpecifications());
//        }
//        if (productInRepository.getStockpile() != null) {
//            productInRepository.setStockpile(updatedProduct.getStockpile());
//        }
//        productRepository.save(productInRepository);
        return "更新成功";
//    else throw new RuntimeException("product not found, id:"+id);
    //return null;
    }

    private void updateProductBasicInfo(Product product, ProductVO productVO) {
        if (productVO.getTitle() != null) {
            product.setTitle(productVO.getTitle());
        }
        if (productVO.getPrice() != null) {
            product.setPrice(productVO.getPrice());
        }
        if (productVO.getRate() != null) {
            product.setRate(productVO.getRate());
        }
        if (productVO.getDescription() != null) {
            product.setDescription(productVO.getDescription());
        }
        if (productVO.getCover() != null) {
            product.setCover(productVO.getCover());
        }
        if (productVO.getDetail() != null) {
            product.setDetail(productVO.getDetail());
        }
        if(productVO.getId() != null) {
            product.setId(productVO.getId());
        }

    }


    @Override
    public void deleteProduct(Integer id) {
        if(!productRepository.existsById(id)) {
            throw TomatoMallException.productNotFound();
        }
        productRepository.deleteById(id);
    }

    /**
     *
     * 疑问：为什么只可以修改amount？这样的话冻结数就不能修改了？
     */
    @Override
    public void adjustStockPile(Integer id, Integer amount) {
        Product product = productRepository.findById(id).orElseThrow(TomatoMallException::productNotFound);
        product.getStockpile().setAmount(amount);
        productRepository.save(product);
    }

    @Override
    public ProductVO.StockpileVO getStockPile(Integer productID){
        Product product = productRepository.findById(productID).orElseThrow(TomatoMallException::productNotFound);

        return product.getStockpile().toStockpileVO();
    }

    public void addStockPile(ProductVO.StockpileVO stockpileVO){
        int productID = stockpileVO.getProductId();
        Product product = productRepository.findById(productID).orElseThrow(TomatoMallException::productNotFound);
        product.setStockpile(stockpileVO.toPO());
        productRepository.save(product);
    }

}
