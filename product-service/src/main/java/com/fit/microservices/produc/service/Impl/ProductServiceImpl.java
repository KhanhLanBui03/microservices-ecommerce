package com.fit.microservices.produc.service.Impl;

import com.fit.microservices.produc.dto.ProductRequest;
import com.fit.microservices.produc.dto.ProductResponse;
import com.fit.microservices.produc.model.Category;
import com.fit.microservices.produc.model.Product;
import com.fit.microservices.produc.repository.CategoryRepository;
import com.fit.microservices.produc.repository.ProductRepository;
import com.fit.microservices.produc.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    @Override
    @Cacheable("allProducts")
    public List<ProductResponse> findAll() {
        System.out.println("Querying DB ...");
        return productRepository.findAll()
                .stream()
                .map(product -> new ProductResponse(product.getName(),product.getDescription(),product.getSkuCode(),product.getPrice(),product.getCategory().getName()))
                .collect(Collectors.toList());
    }

    @Override
    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }

    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    public ProductResponse save(ProductRequest productRequest) {
        Product product = new Product();
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setSkuCode(productRequest.getSkuCode());
        product.setPrice(productRequest.getPrice());
        Category category = categoryRepository.findById(productRequest.getCategoryId()).orElseThrow(() -> new RuntimeException("Thương hiệu không tồn tại"));
        product.setCategory(category);
        productRepository.save(product);
        log.info("Product save success.");
        return new ProductResponse(product.getName(), product.getDescription(), product.getSkuCode(), product.getPrice(), product.getCategory().getName());
    }

    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    public void deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product không tồn tại"));
        productRepository.delete(product);
        log.info("Product delete success.");
    }

    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    public ProductResponse update(Long id, ProductRequest productRequest) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product không tồn tại"));
        if(productRequest.getName()!=null){
            product.setName(productRequest.getName());
        }
        if(productRequest.getDescription()!=null){
            product.setDescription(productRequest.getDescription());
        }
        if(productRequest.getSkuCode()!=null){
            product.setSkuCode(productRequest.getSkuCode());
        }
        if(productRequest.getPrice()!=null){
            product.setPrice(productRequest.getPrice());
        }
        if(productRequest.getCategoryId()!=null){
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(()->new RuntimeException("Danh muc khong ton tai"));
            product.setCategory(category);
        }
        Product updatedProduct = productRepository.save(product);
        return new ProductResponse(
                updatedProduct.getName(),
                updatedProduct.getDescription(),
                updatedProduct.getSkuCode(),
                updatedProduct.getPrice(),
                updatedProduct.getCategory().getName()
        );
    }
}
