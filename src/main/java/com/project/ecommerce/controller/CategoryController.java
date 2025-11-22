package com.project.ecommerce.controller;

import com.project.ecommerce.config.AppConstants;
import com.project.ecommerce.payload.CategoryDTO;
import com.project.ecommerce.payload.CategoryResponse;
import com.project.ecommerce.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Tag(name = "Category APIs", description = "APIs for managing categories")
    @GetMapping("/public/categories")
    public ResponseEntity<CategoryResponse> getAllCategories(@RequestParam(name = "pageNumber", defaultValue =
                                                                     AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                             @RequestParam(name = "pageSize", defaultValue =
                                                                     AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                             @RequestParam(name = "sortBy", defaultValue =
                                                                     AppConstants.SORT_CATEGORIES_BY, required =
                                                                     false) String sortBy,
                                                             @RequestParam(name = "sortOrder", defaultValue =
                                                                     AppConstants.SORT_DIR, required = false) String sortOrder) {
        CategoryResponse categories = categoryService.getAllCategories(pageNumber,
                pageSize,
                sortBy,
                sortOrder);
        return new ResponseEntity<>(categories,
                HttpStatus.OK);
    }

    @Tag(name = "Category APIs", description = "APIs for managing categories")
    @Operation(summary = "Create category", description = "API to create a new category")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Category is created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid Input", content = @Content),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content)
    })
    @PostMapping("/public/categories")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO category) {
        CategoryDTO savedCategoryDTO = categoryService.createCategory(category);
        return new ResponseEntity<>(savedCategoryDTO,
                HttpStatus.CREATED);
    }

    @DeleteMapping("/admin/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> deleteCategory(@Parameter(description = "ID of the category you wish to " +
            "delete") @PathVariable Long categoryId) {
        CategoryDTO categoryDTO = categoryService.deleteCategory(categoryId);
        return new ResponseEntity<>(categoryDTO,
                HttpStatus.OK);
    }


    @PutMapping("/public/categories/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@Valid @RequestBody CategoryDTO category,
                                                      @PathVariable Long categoryId) {
        CategoryDTO savedcategoryDTO = categoryService.updateCategory(category,
                categoryId);
        return new ResponseEntity<>(savedcategoryDTO,
                HttpStatus.OK);
    }
}
