package com.project.ecommerce.service;

import com.project.ecommerce.exceptions.APIException;
import com.project.ecommerce.exceptions.ResourceNotFoundException;
import com.project.ecommerce.model.Category;
import com.project.ecommerce.payload.CategoryDTO;
import com.project.ecommerce.payload.CategoryResponse;
import com.project.ecommerce.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl
        implements CategoryService {

    private final CategoryRepository categoryRepository;

    private final ModelMapper modelMapper;

    public CategoryServiceImpl(CategoryRepository categoryRepository,
                               ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber,
                                             Integer pageSize,
                                             String sortBy,
                                             String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy)
                .ascending() : Sort.by(sortBy)
                .descending();
        Pageable pageDetails = PageRequest.of(pageNumber,
                pageSize,
                sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> allCategories = categoryPage.getContent();
        if (allCategories.isEmpty()) throw new APIException("There is no category present ");
        List<CategoryDTO> categoryDTOs = allCategories.stream()
                .map(category -> modelMapper.map(category,
                        CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOs);
        categoryResponse.setPageNumber(categoryPage.getNumber());
        categoryResponse.setPageSize(categoryPage.getSize());
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO,
                Category.class);
        Category savedCategory = categoryRepository.findByCategoryName(categoryDTO.getCategoryName());
        if (savedCategory != null)
            throw new APIException("Category with the name " + category.getCategoryName() + " already exists !!!");
        Category save = categoryRepository.save(category);
        return modelMapper.map(save,
                CategoryDTO.class);
    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category",
                        "categoryId",
                        categoryId));

        categoryRepository.delete(category);
        return modelMapper.map(category,
                CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO,
                                      Long categoryId) {
        Category category = modelMapper.map(categoryDTO,
                Category.class);
        Category savedCategory = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category",
                        "categoryId",
                        categoryId));
        category.setCategoryId(categoryId);
        savedCategory = categoryRepository.save(category);
        return modelMapper.map(savedCategory,
                CategoryDTO.class);
    }
}
