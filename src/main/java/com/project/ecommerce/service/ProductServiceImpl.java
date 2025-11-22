package com.project.ecommerce.service;

import com.project.ecommerce.exceptions.APIException;
import com.project.ecommerce.exceptions.ResourceNotFoundException;
import com.project.ecommerce.model.Cart;
import com.project.ecommerce.model.Category;
import com.project.ecommerce.model.Product;
import com.project.ecommerce.payload.CartDTO;
import com.project.ecommerce.payload.ProductDTO;
import com.project.ecommerce.payload.ProductResponse;
import com.project.ecommerce.repositories.CartRepository;
import com.project.ecommerce.repositories.CategoryRepository;
import com.project.ecommerce.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductServiceImpl
        implements ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private FileService fileService;
    @Value("${project.image}")
    private String path;

    @Override
    public ProductDTO addProduct(Long categoryId,
                                 ProductDTO productDTO) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category",
                        "categoryId",
                        categoryId));
        boolean isProductNotPresent = true;
        List<Product> products = category.getProducts();
        for (Product value : products) {
            if (value.getProductName()
                    .equals(productDTO.getProductName())) {
                isProductNotPresent = false;
                break;
            }
        }
        if (isProductNotPresent) {
            Product product = modelMapper.map(productDTO,
                    Product.class);
            product.setImage("default.png");
            product.setCategory(category);
            double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
            product.setSpecialPrice(specialPrice);
            Product savedProduct = productRepository.save(product);
            return modelMapper.map(savedProduct,
                    ProductDTO.class);
        } else {
            throw new APIException("Product already exists!!");
        }
    }

    @Override
    public ProductResponse getAllProducts(Integer pageNumber,
                                          Integer pageSize,
                                          String sortBy,
                                          String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy)
                .ascending() : Sort.by(sortBy)
                .descending();

        Pageable pageDetails = PageRequest.of(pageNumber,
                pageSize,
                sortByAndOrder);
        Page<Product> pageProducts = productRepository.findAll(pageDetails);
        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product,
                        ProductDTO.class))
                .toList();
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchByCategory(Long categoryId,
                                            Integer pageNumber,
                                            Integer pageSize,
                                            String sortBy,
                                            String sortOrder) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category",
                        "categoryId",
                        categoryId));
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy)
                .ascending() : Sort.by(sortBy)
                .descending();

        Pageable pageDetails = PageRequest.of(pageNumber,
                pageSize,
                sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByCategoryOrderByPriceAsc(category,
                pageDetails);
        List<Product> products = pageProducts.getContent();
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product,
                        ProductDTO.class))
                .toList();
        if (products.isEmpty()) {
            throw new APIException(category.getCategoryName() + " category does not have any products");
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductResponse searchProductByKeyword(String keyword,
                                                  Integer pageNumber,
                                                  Integer pageSize,
                                                  String sortBy,
                                                  String sortOrder) {

        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ? Sort.by(sortBy)
                .ascending() : Sort.by(sortBy)
                .descending();

        Pageable pageDetails = PageRequest.of(pageNumber,
                pageSize,
                sortByAndOrder);
        Page<Product> pageProducts = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%',
                pageDetails);
        List<Product> products = pageProducts.getContent();

        List<ProductDTO> productDTOs = products.stream()
                .map(product -> modelMapper.map(product,
                        ProductDTO.class))
                .toList();
        if (products.isEmpty()) {
            throw new APIException("Products not found with keyword " + keyword);
        }
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOs);
        productResponse.setPageNumber(pageProducts.getNumber());
        productResponse.setPageSize(pageProducts.getSize());
        productResponse.setTotalElements(pageProducts.getTotalElements());
        productResponse.setTotalPages(pageProducts.getTotalPages());
        productResponse.setLastPage(pageProducts.isLast());
        return productResponse;
    }

    @Override
    public ProductDTO updateProduct(Long productId,
                                    ProductDTO productDTO) {
        //Get the products from DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",
                        "productId",
                        productId));
        Product product = modelMapper.map(productDTO,
                Product.class);
        //update the product info with user shared
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDescription(product.getDescription());
        productFromDb.setQuantity(product.getQuantity());
        productFromDb.setProductName(product.getProductName());
        productFromDb.setDiscount(product.getDiscount());
        productFromDb.setPrice(product.getPrice());
        double specialPrice = product.getPrice() - ((product.getDiscount() * 0.01) * product.getPrice());
        productFromDb.setSpecialPrice(specialPrice);
        //save to db
        Product savedProduct = productRepository.save(productFromDb);

        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        List<CartDTO> cartDTOs = carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart,
                            CartDTO.class);
                    List<ProductDTO> products = cart.getCartItems()
                            .stream()
                            .map(p -> modelMapper.map(p.getProduct(),
                                    ProductDTO.class))
                            .toList();
                    cartDTO.setProducts(products);
                    return cartDTO;
                })
                .toList();
        cartDTOs.forEach(cart -> cartService.updateProductInCarts(cart.getCartId(),
                productId));

        return modelMapper.map(savedProduct,
                ProductDTO.class);
    }

    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",
                        "productId",
                        productId));
        // DELETE
        List<Cart> carts = cartRepository.findCartsByProductId(productId);
        carts.forEach(cart -> cartService.deleteProductFromCart(cart.getCartId(),
                productId));
        productRepository.deleteById(productId);
        return modelMapper.map(product,
                ProductDTO.class);
    }

    @Override
    public ProductDTO updateProductImage(Long productId,
                                         MultipartFile image)
            throws IOException {
        //Get Product from DB
        Product productFromDb = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",
                        "productId",
                        productId));
        //Upload image to server
        //Get the file name of uploaded image
        String fileName = fileService.uploadImage(path,
                image);
        //updating the new file name to the product
        productFromDb.setImage(fileName);
        //Save updated Product
        Product updatedProduct = productRepository.save(productFromDb);
        //return DTO after mapping product to DTO
        return modelMapper.map(updatedProduct,
                ProductDTO.class);
    }
}
