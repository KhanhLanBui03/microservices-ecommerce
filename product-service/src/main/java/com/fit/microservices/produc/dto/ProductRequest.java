package com.fit.microservices.produc.dto;

import com.fit.microservices.produc.model.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ProductRequest {
    private String name;
    private String description;
    private String skuCode;
    private BigDecimal price;
    private Long categoryId;
}
