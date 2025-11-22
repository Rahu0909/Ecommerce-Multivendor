package com.project.ecommerce.controller;

import com.project.ecommerce.config.AppConstants;
import com.project.ecommerce.payload.ProductDTO;
import com.project.ecommerce.payload.ProductResponse;
import com.project.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    ProductService productService;

    @PostMapping("/admin/categories/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@Valid @RequestBody ProductDTO productDTO,
                                                 @PathVariable Long categoryId) {
        ProductDTO savedproductDTO = productService.addProduct(categoryId,
                productDTO);
        return new ResponseEntity<>(savedproductDTO,
                HttpStatus.CREATED);
    }

    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(@RequestParam(name = "pageNumber", defaultValue =
                                                                  AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                          @RequestParam(name = "pageSize", defaultValue =
                                                                  AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                          @RequestParam(name = "sortBy", defaultValue =
                                                                  AppConstants.SORT_PRODUCTS_BY, required = false) String sortBy,
                                                          @RequestParam(name = "sortOrder", defaultValue =
                                                                  AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber,
                pageSize,
                sortBy,
                sortOrder);
        return new ResponseEntity<>(productResponse,
                HttpStatus.OK);
    }

    @GetMapping("/public/categories/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(@PathVariable Long categoryId,
                                                                 @RequestParam(name = "pageNumber", defaultValue =
                                                                         AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                 @RequestParam(name = "pageSize", defaultValue =
                                                                         AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                 @RequestParam(name = "sortBy", defaultValue =
                                                                         AppConstants.SORT_PRODUCTS_BY, required =
                                                                         false) String sortBy,
                                                                 @RequestParam(name = "sortOrder", defaultValue =
                                                                         AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchByCategory(categoryId,
                pageNumber,
                pageSize,
                sortBy,
                sortOrder);
        return new ResponseEntity<>(productResponse,
                HttpStatus.OK);
    }

    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> getProductsByKeyword(@PathVariable String keyword,
                                                                @RequestParam(name =
                                                                        "pageNumber", defaultValue =
                                                                        AppConstants.PAGE_NUMBER, required = false) Integer pageNumber,
                                                                @RequestParam(name = "pageSize", defaultValue =
                                                                        AppConstants.PAGE_SIZE, required = false) Integer pageSize,
                                                                @RequestParam(name = "sortBy", defaultValue =
                                                                        AppConstants.SORT_PRODUCTS_BY, required =
                                                                        false) String sortBy,
                                                                @RequestParam(name = "sortOrder", defaultValue =
                                                                        AppConstants.SORT_DIR, required = false) String sortOrder) {
        ProductResponse productResponse = productService.searchProductByKeyword('%' + keyword + '%',
                pageNumber,
                pageSize,
                sortBy,
                sortOrder);
        return new ResponseEntity<>(productResponse,
                HttpStatus.FOUND);
    }

    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@Valid @RequestBody ProductDTO productDTO,
                                                    @PathVariable Long productId) {
        ProductDTO updatedProductDto = productService.updateProduct(productId,
                productDTO);
        return new ResponseEntity<>(updatedProductDto,
                HttpStatus.OK);
    }

    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProduct = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProduct,
                HttpStatus.OK);
    }

    @PutMapping("/admin/products/{productId}/image")
    public ResponseEntity<ProductDTO> updateProductImage(@PathVariable Long productId,
                                                         @RequestParam("image") MultipartFile image)
            throws IOException {
        ProductDTO updatedProductImage = productService.updateProductImage(productId,
                image);
        return new ResponseEntity<>(updatedProductImage,
                HttpStatus.OK);
    }
}
