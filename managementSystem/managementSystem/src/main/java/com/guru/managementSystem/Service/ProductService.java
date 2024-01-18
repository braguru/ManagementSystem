package com.guru.managementSystem.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.guru.managementSystem.Entity.Product;
import com.guru.managementSystem.Repository.ProductRepository;

// Business logic(logic for the application)

@Service
public class ProductService {
    @Autowired
    private ProductRepository repository;

    // Business logic for POST Methods
    public Product addProduct(Product product){
        return repository.save(product);
    }

    public List<Product> addProducts(List<Product> products){
        return repository.saveAll(products);
    }
    // ---------------------------------------------------- //

    // Business logic for GET Methods
    public List<Product> getProducts(){
        return repository.findAll();
    }

    public Product getProductById(int id){
        return repository.findById(id).orElse(null);
    }

    public Product getProductByName(String name){
        return repository.findByName(name).orElse(null);
    }
    // ----------------------------------------------------- //


    // Business logic for DELETE Method
    public String deleteProduct(int id){
        repository.deleteById(id);
        return "Successfully deleted" + id;
    }

    public String deleteProducts(Product product){
        repository.delete(product);
        return "Products deleted";
    }
    // ---------------------------------------------------- //


    // Business logic for UPDATE Methods
    public Product updateProduct(Product product){
        Product existingProduct = repository.findById(product.getId()).orElse(null);
        existingProduct.setName(product.getName());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setPrice(product.getPrice());
        return repository.save(existingProduct);
    }
}