package com.project.ecommerce.service;

import com.project.ecommerce.exceptions.APIException;
import com.project.ecommerce.exceptions.ResourceNotFoundException;
import com.project.ecommerce.model.Cart;
import com.project.ecommerce.model.CartItem;
import com.project.ecommerce.model.Product;
import com.project.ecommerce.payload.CartDTO;
import com.project.ecommerce.payload.ProductDTO;
import com.project.ecommerce.repositories.CartItemRepository;
import com.project.ecommerce.repositories.CartRepository;
import com.project.ecommerce.repositories.ProductRepository;
import com.project.ecommerce.utils.AuthUtil;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl
        implements CartService {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AuthUtil authUtil;

    @Override
    public CartDTO addProductToCart(Long productId,
                                    Integer quantity) {
        // Find existing cart or create one
        Cart cart = createCart();
        // Retrieve Product Details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",
                        "productId",
                        productId));
        // Perform Validations
        cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),
                productId);
        if (cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(),
                productId) != null) {
            throw new APIException("Product " + product.getProductName() + " already exists");
        }
        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }
        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName() + " less than or equal "
                    + "to the quantity " + product.getQuantity() + ".");
        }
        // Create Cart Item
        CartItem newCartItem = new CartItem();
        newCartItem.setProduct(product);
        newCartItem.setCart(cart);
        newCartItem.setQuantity(quantity);
        newCartItem.setDiscount(product.getDiscount());
        newCartItem.setProductPrice(product.getSpecialPrice());
        cartItemRepository.save(newCartItem);

        product.setQuantity(product.getQuantity());
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);

        // Return updated Cart
        CartDTO cartDTO = modelMapper.map(cart,
                CartDTO.class);
        List<CartItem> cartItems = cart.getCartItems();
        Stream<ProductDTO> productDTOStream = cartItems.stream()
                .map(item -> {
                    ProductDTO map = modelMapper.map(item.getProduct(),
                            ProductDTO.class);
                    map.setQuantity(item.getQuantity());
                    return map;
                });
        cartDTO.setProducts(productDTOStream.toList());
        return cartDTO;
    }

    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        if (carts.isEmpty()) {
            throw new APIException("No carts found");
        }

        return carts.stream()
                .map(cart -> {
                    CartDTO cartDTO = modelMapper.map(cart,
                            CartDTO.class);

                    List<ProductDTO> products = cart.getCartItems()
                            .stream()
                            .map(cartItem -> {
                                ProductDTO productDTO = modelMapper.map(cartItem.getProduct(),
                                        ProductDTO.class);
                                productDTO.setQuantity(cartItem.getQuantity());
                                return productDTO;
                            })
                            .toList();
                    cartDTO.setProducts(products);
                    return cartDTO;
                })
                .toList();
    }

    @Override
    public CartDTO getCart(String emailId,
                           Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId,
                cartId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart",
                    "cartId",
                    cartId);
        }
        CartDTO cartDTO = modelMapper.map(cart,
                CartDTO.class);
        cart.getCartItems()
                .forEach(c -> c.getProduct()
                        .setQuantity(c.getQuantity()));
        List<ProductDTO> products = cart.getCartItems()
                .stream()
                .map(p -> modelMapper.map(p.getProduct(),
                        ProductDTO.class))
                .toList();
        cartDTO.setProducts(products);
        return cartDTO;
    }

    @Override
    @Transactional
    public CartDTO updateProductQuantity(Long productId,
                                         Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();

        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart",
                        "cartId",
                        cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",
                        "productId",
                        productId));

        if (product.getQuantity() == 0) {
            throw new APIException(product.getProductName() + " is not available");
        }

        if (product.getQuantity() < quantity) {
            throw new APIException("Please, make an order of the " + product.getProductName()
                    + " less than or equal to the quantity " + product.getQuantity() + ".");
        }

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,
                productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        // Calculate new quantity
        int newQuantity = cartItem.getQuantity() + quantity;

        // Validation to prevent negative quantities
        if (newQuantity < 0) {
            throw new APIException("The resulting quantity cannot be negative.");
        }

        if (newQuantity == 0) {
            deleteProductFromCart(cartId,
                    productId);
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (cartItem.getProductPrice() * quantity));
            cartRepository.save(cart);
        }

        CartItem updatedItem = cartItemRepository.save(cartItem);
        if (updatedItem.getQuantity() == 0) {
            cartItemRepository.deleteById(updatedItem.getCartItemId());
        }


        CartDTO cartDTO = modelMapper.map(cart,
                CartDTO.class);

        List<CartItem> cartItems = cart.getCartItems();

        Stream<ProductDTO> productStream = cartItems.stream()
                .map(item -> {
                    ProductDTO prd = modelMapper.map(item.getProduct(),
                            ProductDTO.class);
                    prd.setQuantity(item.getQuantity());
                    return prd;
                });


        cartDTO.setProducts(productStream.toList());

        return cartDTO;
    }

    @Override
    @Transactional
    public String deleteProductFromCart(Long cartId,
                                        long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart",
                        "cartId",
                        cartId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,
                productId);

        if (cartItem == null) {
            throw new ResourceNotFoundException("Product",
                    "productId",
                    productId);
        }

        cart.setTotalPrice(cart.getTotalPrice() -
                (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId,
                productId);

        return "Product " + cartItem.getProduct()
                .getProductName() + " removed from the cart !!!";
    }

    @Override
    public void updateProductInCarts(Long cartId,
                                     Long productId) {
        Cart cart = cartRepository.findById(cartId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart",
                        "cartId",
                        cartId));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product",
                        "productId",
                        productId));

        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId,
                productId);

        if (cartItem == null) {
            throw new APIException("Product " + product.getProductName() + " not available in the cart!!!");
        }

        double cartPrice = cart.getTotalPrice()
                - (cartItem.getProductPrice() * cartItem.getQuantity());

        cartItem.setProductPrice(product.getSpecialPrice());

        cart.setTotalPrice(cartPrice
                + (cartItem.getProductPrice() * cartItem.getQuantity()));

        cartItem = cartItemRepository.save(cartItem);
    }

    private Cart createCart() {
        Cart userCart = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if (userCart != null) {
            return userCart;
        }

        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        return cartRepository.save(cart);
    }
}
