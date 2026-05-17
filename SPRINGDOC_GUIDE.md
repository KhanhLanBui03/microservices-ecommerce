# SpringDoc OpenAPI Documentation Guide

## 📚 Overview

This guide explains how to use **SpringDoc OpenAPI** in the Microservices E-commerce project for automatic API documentation generation.

## 🎯 What is SpringDoc OpenAPI?

**SpringDoc OpenAPI** is a Spring Boot integration library that:
- Automatically generates **OpenAPI 3.0** specification from your Spring Boot application
- Provides **Swagger UI** for interactive API testing
- Requires minimal configuration
- Keeps documentation in sync with code automatically

## 🚀 Quick Start

### 1. Access Swagger UI

Start any service and navigate to:
```
http://localhost:<service-port>/swagger-ui.html
```

Example:
- Product Service: `http://localhost:8003/swagger-ui.html`
- Order Service: `http://localhost:8004/swagger-ui.html`

### 2. View OpenAPI JSON

```
http://localhost:<service-port>/v3/api-docs
```

### 3. Test API Endpoints

- Click **Try it out** on any endpoint
- Fill in parameters
- Click **Execute**
- View response

---

## 📝 Annotations Reference

### 1. `@Tag` - Group Endpoints

**Location:** Class level

```java
@Tag(name = "Product API", description = "Product Catalog Management")
@RestController
@RequestMapping("/api/product")
public class ProductController {
    // ...
}
```

**Result in Swagger:**
- Creates a section called "Product API"
- All endpoints in this controller grouped together

---

### 2. `@Operation` - Describe Endpoint

**Location:** Method level

```java
@Operation(summary = "Create new product",
           description = "Create a new product in the catalog (Admin only). Returns the created product with ID.")
@PostMapping
public ProductResponse createProduct(@RequestBody ProductRequest request) {
    // ...
}
```

**Result in Swagger:**
- Summary shows as brief operation title
- Description shows when expanded
- Better understanding of what endpoint does

---

### 3. `@ApiResponses` & `@ApiResponse` - Document Status Codes

**Location:** Method level

```java
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", 
                 description = "Product created successfully",
                 content = @Content(schema = @Schema(implementation = ProductResponse.class))),
    @ApiResponse(responseCode = "400", 
                 description = "Invalid product data - validation failed"),
    @ApiResponse(responseCode = "403", 
                 description = "Insufficient permissions - Admin role required"),
    @ApiResponse(responseCode = "409", 
                 description = "Conflict - Product already exists")
})
@PostMapping
public ProductResponse createProduct(@RequestBody ProductRequest request) {
    // ...
}
```

**Result in Swagger:**
- Shows all possible response codes
- Descriptions for each code
- Example response bodies

---

### 4. `@Parameter` - Document Path/Query Parameters

**Location:** Parameter level

```java
@GetMapping("/{id}")
public ResponseEntity<ProductResponse> getProduct(
    @Parameter(description = "Product ID to retrieve. Must be a positive integer.",
               example = "123")
    @PathVariable Long id) {
    // ...
}
```

**Result in Swagger:**
- Parameter description shown
- Example value provided
- Type and constraints documented

---

### 5. `@RequestBody` with Schema

**Location:** Method parameter

```java
@PostMapping
@Operation(summary = "Create product")
public ProductResponse create(
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
        description = "Product details to create",
        required = true,
        content = @Content(schema = @Schema(implementation = ProductRequest.class)))
    @RequestBody ProductRequest request) {
    // ...
}
```

**Result in Swagger:**
- Request body schema shown
- Required fields highlighted
- Example request body generated

---

### 6. `@SecurityRequirement` - Mark Protected Endpoints

**Location:** Method level

```java
@SecurityRequirement(name = "bearer-jwt")
@PreAuthorize("hasRole('ADMIN')")
@PostMapping
public ProductResponse createProduct(@RequestBody ProductRequest request) {
    // ...
}
```

**Result in Swagger:**
- 🔒 Lock icon shown on endpoint
- Authorization required to test
- JWT token can be provided via Authorize button

---

### 7. `@Schema` - Document DTO Classes

**Location:** Class or field level

```java
@Schema(description = "Product information response")
@Data
public class ProductResponse {
    
    @Schema(description = "Unique product identifier",
            example = "123",
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Product name",
            example = "Laptop",
            minLength = 1,
            maxLength = 255,
            required = true)
    private String name;
    
    @Schema(description = "Product price in USD",
            example = "999.99",
            minimum = "0.01",
            required = true)
    private BigDecimal price;
}
```

**Result in Swagger:**
- Field descriptions shown
- Example values displayed
- Validation rules documented
- Read-only/write-only fields marked

---

## 🔐 JWT Authentication

### Setup in OpenAPI Config

```java
@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .components(new Components()
                .addSecuritySchemes("bearer-jwt", new SecurityScheme()
                    .type(SecurityScheme.Type.HTTP)
                    .scheme("bearer")
                    .bearerFormat("JWT")
                    .description("Enter JWT token")))
            .addSecurityItem(new SecurityRequirement().addList("bearer-jwt"));
    }
}
```

### Using in Swagger UI

1. Get JWT token from `/api/auth/login`
2. Click **Authorize** button (top right)
3. Enter: `Bearer <your-token>`
4. Click **Authorize**
5. All subsequent requests include token automatically

---

## 📊 Complete Example

### Controller with Full Documentation

```java
@Tag(name = "Product API", description = "Product management endpoints")
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @Operation(
        summary = "Create new product",
        description = "Creates a new product in the catalog. Only accessible by administrators.",
        tags = {"Product Creation"}
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "201",
            description = "Product created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ProductResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input - check field validations"
        ),
        @ApiResponse(
            responseCode = "403",
            description = "Access denied - Admin role required"
        )
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createProduct(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product details",
            required = true
        )
        @Valid @RequestBody ProductRequest request) {
        return productService.save(request);
    }

    @Operation(
        summary = "Get all products",
        description = "Retrieve all products from the catalog"
    )
    @ApiResponse(
        responseCode = "200",
        description = "Products retrieved successfully"
    )
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ProductResponse> getAllProducts() {
        return productService.findAll();
    }

    @Operation(
        summary = "Get product by ID",
        description = "Retrieve a specific product by its unique identifier"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "Product found"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Product not found"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(
        @Parameter(
            description = "Product ID",
            example = "123",
            required = true
        )
        @PathVariable Long id) {
        return ResponseEntity.ok(productService.findById(id));
    }
}
```

### DTO with Schema Documentation

```java
@Schema(description = "Product creation request")
@Data
@Valid
public class ProductRequest {
    
    @Schema(
        description = "Product name",
        example = "Wireless Mouse",
        minLength = 1,
        maxLength = 255,
        required = true
    )
    @NotBlank(message = "Product name cannot be blank")
    private String name;
    
    @Schema(
        description = "Product description",
        example = "High precision wireless mouse with 2.4GHz connectivity"
    )
    @NotBlank
    private String description;
    
    @Schema(
        description = "Product price in USD",
        example = "29.99",
        minimum = "0.01",
        required = true
    )
    @DecimalMin(value = "0.01", message = "Price must be greater than 0")
    private BigDecimal price;
    
    @Schema(
        description = "Available quantity in stock",
        example = "100",
        minimum = "0",
        required = true
    )
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;
}
```

---

## 🎨 Customization

### Customize Service Info

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .info(new Info()
            .title("My Product Service")
            .description("Manages product catalog")
            .version("v1.0.0")
            .termsOfService("https://example.com/terms")
            .contact(new Contact()
                .name("API Support")
                .url("https://example.com/support")
                .email("support@example.com"))
            .license(new License()
                .name("Apache 2.0")
                .url("https://www.apache.org/licenses/LICENSE-2.0.html")))
        .externalDocs(new ExternalDocumentation()
            .description("Full Documentation")
            .url("https://docs.example.com"));
}
```

### Add Server URLs

```java
@Bean
public OpenAPI customOpenAPI() {
    return new OpenAPI()
        .addServersItem(new Server()
            .url("http://localhost:8003")
            .description("Local Development"))
        .addServersItem(new Server()
            .url("https://api.staging.example.com")
            .description("Staging Environment"))
        .addServersItem(new Server()
            .url("https://api.example.com")
            .description("Production"));
}
```

---

## 🔗 Links

- [SpringDoc OpenAPI GitHub](https://github.com/springdoc/springdoc-openapi)
- [OpenAPI 3.0 Specification](https://spec.openapis.org/oas/v3.0.3)
- [Swagger UI](https://swagger.io/tools/swagger-ui/)

---

## ✅ Checklist for API Documentation

- [ ] All controllers have `@Tag` annotation
- [ ] All public methods have `@Operation` annotation
- [ ] All endpoints document response codes with `@ApiResponses`
- [ ] Protected endpoints have `@SecurityRequirement`
- [ ] Path/query parameters documented with `@Parameter`
- [ ] Request/response DTOs have `@Schema` annotations
- [ ] Validation annotations match schema constraints
- [ ] Examples provided in `@Schema` where helpful
- [ ] Error responses documented
- [ ] Tested in Swagger UI

---

**Last Updated:** January 2026  
**Status:** Production Ready
