package com.fit.microservice.mcp.tool;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fit.microservice.mcp.client.ProductClient;
import com.fit.microservice.mcp.dto.ProductResponse;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ProductMcpTool {

    private final ProductClient productClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ProductMcpTool(ProductClient productClient) {
        this.productClient = productClient;
    }

    @Tool(
            name = "listProducts",
            description = "Lấy danh sách sản phẩm hiện có trong hệ thống"
    )
    public String listProducts() throws JsonProcessingException {

        List<ProductResponse> products = productClient.getAllProducts();

        Map<String, Object> response = Map.of(
                "total", products.size(),
                "products", products
        );

        return objectMapper.writeValueAsString(response);
    }

}
