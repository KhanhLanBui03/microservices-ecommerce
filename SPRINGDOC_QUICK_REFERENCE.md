# 🔍 SpringDoc Quick Reference

## 📍 Swagger UI URLs

| Service | URL |
|---------|-----|
| Auth Service | http://localhost:8001/swagger-ui.html |
| User Service | http://localhost:8002/swagger-ui.html |
| Product Service | http://localhost:8003/swagger-ui.html |
| Order Service | http://localhost:8004/swagger-ui.html |
| Payment Service | http://localhost:8005/swagger-ui.html |
| Inventory Service | http://localhost:8006/swagger-ui.html |

---

## 🎯 Common Annotations

### Endpoint Documentation
```java
@Tag(name = "API Name")                        // Group endpoints
@Operation(summary = "What it does")           // Describe operation
@ApiResponses({ ... })                         // Document responses
@SecurityRequirement(name = "bearer-jwt")      // Mark protected
@Parameter(description = "...")                // Document parameters
```

### Example
```java
@Tag(name = "Product API")
@RestController
@RequestMapping("/api/product")
public class ProductController {
    
    @Operation(summary = "Create product")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Created"),
        @ApiResponse(responseCode = "400", description = "Invalid data"),
        @ApiResponse(responseCode = "403", description = "No permission")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ProductResponse create(@RequestBody ProductRequest req) {
        // ...
    }
}
```

---

## 🔐 Testing with JWT

1. **Get Token:**
   ```bash
   curl -X POST http://localhost:8001/api/auth/login \
     -H "Content-Type: application/json" \
     -d '{"username":"admin","password":"password"}'
   ```

2. **In Swagger UI:**
   - Click **Authorize** (top right)
   - Paste: `Bearer <token>`
   - Click **Authorize**

3. **Test Protected Endpoints:**
   - Now test any endpoint with 🔒 icon

---

## 📊 DTO Documentation

```java
@Schema(description = "Product response")
@Data
public class ProductResponse {
    
    @Schema(description = "Product ID", example = "123", 
            accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;
    
    @Schema(description = "Product name", example = "Laptop",
            minLength = 1, maxLength = 255)
    private String name;
    
    @Schema(description = "Product price", example = "999.99",
            minimum = "0.01")
    private BigDecimal price;
}
```

---

## 🚀 Quick Start Template

```java
@Tag(name = "Your API", description = "Description")
@RestController
@RequestMapping("/api/your-endpoint")
@RequiredArgsConstructor
public class YourController {
    
    @Operation(summary = "Do something")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Bad request"),
        @ApiResponse(responseCode = "404", description = "Not found")
    })
    @SecurityRequirement(name = "bearer-jwt")
    @GetMapping("/{id}")
    public ResponseEntity<?> yourMethod(
        @Parameter(description = "Resource ID") @PathVariable Long id) {
        // Implementation
        return ResponseEntity.ok("Result");
    }
}
```

---

## ✅ Dos and Don'ts

✅ **DO:**
- Document all public endpoints
- Provide meaningful descriptions
- Document error scenarios
- Add examples to schemas
- Use `@Parameter` for path variables
- Mark protected endpoints with `@SecurityRequirement`

❌ **DON'T:**
- Leave endpoints undocumented
- Use generic descriptions like "Get something"
- Forget error response codes
- Mix multiple operations in one method
- Commit code without Swagger docs

---

**Quick Reference | Last Updated: January 2026**
