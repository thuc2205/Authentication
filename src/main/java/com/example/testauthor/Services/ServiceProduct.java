package com.example.testauthor.Services;

import com.example.testauthor.model.Product;
import com.example.testauthor.repositories.ProductRepo;
import jakarta.transaction.Transactional;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class ServiceProduct {
    @Autowired
    private ProductRepo productRepo;

    public  List<Product> getAllProducts() {
        return productRepo.findAll();
    }
    public  Product getProductById(int id) {return productRepo.findById(Long.valueOf(id)).orElse(null);}
    @Transactional
    public  Product addProduct(Product product) {

        return productRepo.save(product);
    }
    @Transactional
    public  Product updateProduct(Product product) {
        return productRepo.save(product);
    }
    @Transactional
    public void deleteProduct(Long id) {
        Optional<Product> product = productRepo.findById(Long.valueOf(id));
        Product product1 = product.get();
        productRepo.delete(product1);

    }



}
