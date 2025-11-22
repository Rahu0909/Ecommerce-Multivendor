package com.project.ecommerce.service;

import com.project.ecommerce.payload.CartDTO;
import jakarta.transaction.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId,
                             Integer quantity);

    List<CartDTO> getAllCarts();

    CartDTO getCart(String emailId,
                    Long cartId);

    @Transactional
    CartDTO updateProductQuantity(Long productId,
                                  Integer quantity);

    @Transactional
    String deleteProductFromCart(Long cartId,
                                 long productId);

    void updateProductInCarts(Long cartId,
                              Long productId);

}
