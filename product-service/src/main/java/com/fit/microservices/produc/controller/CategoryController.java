package com.fit.microservices.produc.controller;

import com.fit.microservices.produc.dto.CategoryRequest;
import com.fit.microservices.produc.dto.CategoryResponse;
import com.fit.microservices.produc.model.Category;
import com.fit.microservices.produc.repository.CategoryRepository;
import com.fit.microservices.produc.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(name = "Category API", description = "Operations related to categories")
@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @Operation(summary = "Get all category")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<CategoryResponse> findAll() {
        return  categoryService.findAll();
    }
    @Operation(summary = "Create new category")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryResponse createCategory(@Valid  @RequestBody CategoryRequest categoryRequest) {
        return categoryService.save(categoryRequest);
    }
    @Operation(summary = "Update category")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryResponse updateCategory(@PathVariable Long id,@Valid  @RequestBody CategoryRequest categoryRequest) {
        return categoryService.update(id, categoryRequest);
    }
    @Operation(summary = "Delete category by ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCategory(@PathVariable Long id) {
        categoryService.deleteById(id);
    }
}
