package com.project.ecommerce.service;

import com.project.ecommerce.payload.ProductDTO;
import com.project.ecommerce.payload.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProductService {
    ProductDTO addProduct(Long categoryId,
                          ProductDTO productDTO);

    ProductResponse getAllProducts(Integer pageNumber,
                                   Integer pageSize,
                                   String sortBy,
                                   String sortOrder);

    ProductResponse searchByCategory(Long categoryId,
                                     Integer pageNumber,
                                     Integer pageSize,
                                     String sortBy,
                                     String sortOrder);

    ProductResponse searchProductByKeyword(String keyword,
                                           Integer pageNumber,
                                           Integer pageSize,
                                           String sortBy,
                                           String sortOrder);

    ProductDTO updateProduct(Long productId,
                             ProductDTO productDTO);

    ProductDTO deleteProduct(Long productId);

    ProductDTO updateProductImage(Long productId,
                                  MultipartFile image)
            throws IOException;
}
