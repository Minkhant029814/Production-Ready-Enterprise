package com.security.jwtdemo.controller.product;

import com.security.jwtdemo.dto.productDto.CreateProductRequest;
import com.security.jwtdemo.dto.productDto.ProductResponse;
import com.security.jwtdemo.entity.mysql.User;
import com.security.jwtdemo.service.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private  final ProductService productService;

    public  ProductController(ProductService productService){
        this.productService = productService;
    }

    //Add new Product for only authenticated users
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @RequestBody CreateProductRequest request,
            Authentication authentication
            ){
        //Getting authenticated user id from security Context
        User currentUser = (User) authentication.getPrincipal();
        ProductResponse response = productService.createProduct(request,currentUser.getId());
        return  ResponseEntity.ok(response);
    }

    //To fetch all Products
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }

    //Get product by id
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id){
        return  ResponseEntity.ok(productService.getProductById(id));
    }
}
