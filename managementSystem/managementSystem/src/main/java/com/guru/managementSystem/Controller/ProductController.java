package com.guru.managementSystem.Controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.guru.managementSystem.Entity.Product;
import com.guru.managementSystem.Service.ProductService;

@RestController
public class ProductController {
    
    @Autowired
    private ProductService service;

    @PostMapping("/addProduct")
    public Product addProduct(@RequestBody Product product){
        return service.addProduct(product);
    }

    @PostMapping("/addProducts")
    public List<Product> addProducts(@RequestBody List<Product> products){
        return service.addProducts(products);
    }

    @GetMapping("/products")
    public List<Product> allProducts(){
        return service.getProducts();
    }

    @GetMapping("/product/{id}")
    public Product productById(@PathVariable int id){
        return service.getProductById(id);
    }

    @GetMapping("/product")
    public Product productByName(@RequestParam(value = "name") String name){
        return service.getProductByName(name);
    }

    @PutMapping("/update")
    public Product updateProduct(@RequestBody Product product){
        return service.updateProduct(product);
    }

    @DeleteMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id){
        return service.deleteProduct(id);
    }
}
