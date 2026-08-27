package com.security.jwtdemo.service.product;

import com.security.jwtdemo.dto.productDto.CreateProductRequest;
import com.security.jwtdemo.dto.productDto.ProductResponse;
import com.security.jwtdemo.entity.mongo.ProductCatalog;
import com.security.jwtdemo.entity.mysql.Product;
import com.security.jwtdemo.respository.mongoRepository.ProductCatalogRepository;
import com.security.jwtdemo.respository.mysqlRepository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private  final ProductRepository productRepository;
    private  final ProductCatalogRepository productCatalogRepository;

    //Constructor Injection
    public  ProductService(ProductRepository productRepository,ProductCatalogRepository productCatalogRepository){
        this.productCatalogRepository = productCatalogRepository;
        this.productRepository = productRepository;
    }

    // To store new product (Hybrid Logic)
    @Transactional // use for Mysql Transaction
    public ProductResponse createProduct(CreateProductRequest request,Long currentUserId){
        Product product = Product.builder()
                .name(request.name())
                .price(request.price())
                .stockQuantity(request.stockQuantity())
                .createdByUserId(currentUserId)
                .build();
        Product savedProduct = productRepository.save(product);

        //Save to MongoDb using MySQL Product ID
        ProductCatalog productCatalog = ProductCatalog.builder()
                .mysqlProductId(savedProduct.getId())
                .description(request.description())
                .attributes(request.attributes())
                .tags(request.tags())
                .averageRating(0.0)
                .build();
        productCatalogRepository.save(productCatalog);

        // Creating Response DTO for Client
        return mapToProductResponse(savedProduct,productCatalog);
    }

    // Fetching All products

    public List<ProductResponse>  getAllProducts(){
        List<Product> products = productRepository.findAll();

        return  products.stream().map(product->{
            ProductCatalog catalog = productCatalogRepository.findByMysqlProductId(product.getId())
                    .orElse(new ProductCatalog());
            return mapToProductResponse(product,catalog);
        }).collect(Collectors.toList());
    }
    // get Single Product by Id
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));

        ProductCatalog catalog = productCatalogRepository.findByMysqlProductId(product.getId())
                .orElse(new ProductCatalog());

        return mapToProductResponse(product, catalog);
    }

    // Helper Method for to combine MySQL + MongoDB to be ProductResponse DTO
    private ProductResponse mapToProductResponse(Product product,ProductCatalog catalog){
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .description(catalog.getDescription())
                .attributes(catalog.getAttributes())
                .tags(catalog.getTags())
                .build();
    }

}
