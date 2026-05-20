package com.fit.microservices.produc.service;

import com.fit.microservices.produc.dto.ProductRequest;
import com.fit.microservices.produc.dto.ProductResponse;
import com.fit.microservices.produc.model.Category;
import com.fit.microservices.produc.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    List<ProductResponse> findAll();
    ProductResponse save(ProductRequest productRequest);
    ProductResponse saveWithFiles(String name, String description, String skuCode,
                                  java.math.BigDecimal price, Long categoryId,
                                  List<MultipartFile> files);
    Product findById(Long id);
    void  deleteById(Long id);
    ProductResponse update(Long id,ProductRequest productRequest);

}
