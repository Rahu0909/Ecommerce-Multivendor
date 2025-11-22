package com.project.ecommerce.service;

import com.project.ecommerce.payload.CategoryDTO;
import com.project.ecommerce.payload.CategoryResponse;

public interface CategoryService {
    CategoryResponse getAllCategories(Integer pageNumber,
                                      Integer pageSize,
                                      String sortBy,
                                      String sortOrder);

    CategoryDTO createCategory(CategoryDTO categoryDTO);

    CategoryDTO deleteCategory(Long categoryId);

    CategoryDTO updateCategory(CategoryDTO category,
                               Long categoryId);
}
