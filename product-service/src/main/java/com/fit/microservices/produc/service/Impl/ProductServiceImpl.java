package com.fit.microservices.produc.service.Impl;

import com.fit.microservices.produc.dto.ImageResponse;
import com.fit.microservices.produc.dto.ProductRequest;
import com.fit.microservices.produc.dto.ProductResponse;
import com.fit.microservices.produc.model.Category;
import com.fit.microservices.produc.model.Image;
import com.fit.microservices.produc.model.Product;
import com.fit.microservices.produc.repository.CategoryRepository;
import com.fit.microservices.produc.repository.ProductRepository;
import com.fit.microservices.produc.service.ProductService;
import com.fit.microservices.produc.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final S3Service s3Service;

    @Override
    @Cacheable(value = "allProducts", key = "'all'")
    public List<ProductResponse> findAll() {
        System.out.println("Querying DB ...");

        return productRepository.findAll()
                .stream()
                .map(product -> {

                    List<ImageResponse> imageResponses = product.getImages()
                            .stream()
                            .map(image -> new ImageResponse(
                                    image.getId(),
                                    image.getUrl()
                            ))
                            .toList();

                    return new ProductResponse(
                            product.getName(),
                            product.getDescription(),
                            product.getSkuCode(),
                            product.getPrice(),
                            product.getCategory().getName(),
                            imageResponses
                    );
                })
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
        List<Image> images = productRequest.getImages()
                .stream()
                .map(img -> {
                    Image image = new Image();
                    image.setName(img.getName());
                    image.setUrl(img.getUrl());
                    image.setProduct(product);
                    return image;
                })
                .toList();
        product.setImages(images);
        Product savedProduct = productRepository.save(product);
        List<ImageResponse> imageResponses = savedProduct.getImages()
                .stream()
                .map(image -> new ImageResponse(
                        image.getId(),
                        image.getUrl()
                )).toList();
        log.info("Product save success.");
        return new ProductResponse(product.getName(), product.getDescription(), product.getSkuCode(), product.getPrice(), product.getCategory().getName(),imageResponses);
    }

    @Override
    @CacheEvict(value = "allProducts", allEntries = true)
    @Transactional
    public ProductResponse saveWithFiles(String name, String description, String skuCode,
                                         BigDecimal price, Long categoryId,
                                         List<MultipartFile> files) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category không tồn tại"));

        Product product = new Product();
        product.setName(name);
        product.setDescription(description);
        product.setSkuCode(skuCode);
        product.setPrice(price);
        product.setCategory(category);

        // Upload từng file lên S3 và tạo Image entity
        List<Image> images = files.stream()
                .map(file -> {
                    String url = s3Service.uploadFile(file);
                    Image image = new Image();
                    image.setName(file.getOriginalFilename());
                    image.setUrl(url);
                    image.setProduct(product);
                    return image;
                })
                .toList();

        product.setImages(images);
        Product savedProduct = productRepository.save(product);

        List<ImageResponse> imageResponses = savedProduct.getImages()
                .stream()
                .map(image -> new ImageResponse(image.getId(), image.getUrl()))
                .toList();

        log.info("Product saved with {} image(s) uploaded to S3.", images.size());
        return new ProductResponse(savedProduct.getName(), savedProduct.getDescription(),
                savedProduct.getSkuCode(), savedProduct.getPrice(),
                savedProduct.getCategory().getName(), imageResponses);
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
    @Transactional
    public ProductResponse update(Long id, ProductRequest productRequest) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product không tồn tại"));
        if (productRequest.getName() != null) {
            product.setName(productRequest.getName());
        }

        if (productRequest.getDescription() != null) {
            product.setDescription(productRequest.getDescription());
        }

        if (productRequest.getSkuCode() != null) {
            product.setSkuCode(productRequest.getSkuCode());
        }

        if (productRequest.getPrice() != null) {
            product.setPrice(productRequest.getPrice());
        }

        if (productRequest.getCategoryId() != null) {
            Category category = categoryRepository.findById(productRequest.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("Category không tồn tại"));
            product.setCategory(category);
        }

        if (productRequest.getImages() != null) {
            product.getImages().clear();
            List<Image> newImages = productRequest.getImages()
                    .stream()
                    .map(img -> {
                        Image image = new Image();
                        image.setName(img.getName());
                        image.setUrl(img.getUrl());
                        image.setProduct(product);
                        return image;
                    })
                    .toList();

            product.getImages().addAll(newImages);
        }

        Product savedProduct = productRepository.save(product);
        List<ImageResponse> imageResponses = savedProduct.getImages()
                .stream()
                .map(image -> new ImageResponse(
                        image.getId(),
                        image.getUrl()
                ))
                .toList();

        return new ProductResponse(
                savedProduct.getName(),
                savedProduct.getDescription(),
                savedProduct.getSkuCode(),
                savedProduct.getPrice(),
                savedProduct.getCategory().getName(),
                imageResponses
        );
    }

}
