package com.project.ecommerce.service;

import com.project.ecommerce.exceptions.APIException;
import com.project.ecommerce.exceptions.ResourceNotFoundException;
import com.project.ecommerce.model.*;
import com.project.ecommerce.payload.OrderDTO;
import com.project.ecommerce.payload.OrderItemDTO;
import com.project.ecommerce.repositories.*;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderServiceImpl
        implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private CartService cartService;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    @Transactional
    public OrderDTO placeOrder(String emailId,
                               Long addressId,
                               String paymentMethod,
                               String pgName,
                               String pgPaymentId,
                               String pgStatus,
                               String pgResponseMessage) {
        Cart cart = cartRepository.findCartByEmail(emailId);
        if (cart == null) {
            throw new ResourceNotFoundException("Cart",
                    "email",
                    emailId);
        }

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address",
                        "addressId",
                        addressId));

        Order order = new Order();
        order.setEmail(emailId);
        order.setOrderDate(LocalDate.now());
        order.setTotalAmount(cart.getTotalPrice());
        order.setOrderStatus("Order Accepted !");
        order.setAddress(address);

        Payment payment = new Payment(paymentMethod,
                pgPaymentId,
                pgStatus,
                pgResponseMessage,
                pgName);
        payment.setOrder(order);
        payment = paymentRepository.save(payment);
        order.setPayment(payment);

        Order savedOrder = orderRepository.save(order);

        List<CartItem> cartItems = cart.getCartItems();
        if (cartItems.isEmpty()) {
            throw new APIException("Cart is empty");
        }

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setDiscount(cartItem.getDiscount());
            orderItem.setOrderedProductPrice(cartItem.getProductPrice());
            orderItem.setOrder(savedOrder);
            orderItems.add(orderItem);
        }

        orderItems = orderItemRepository.saveAll(orderItems);

        cart.getCartItems()
                .forEach(item -> {
                    int quantity = item.getQuantity();
                    Product product = item.getProduct();

                    // Reduce stock quantity
                    product.setQuantity(product.getQuantity() - quantity);

                    // Save product back to the database
                    productRepository.save(product);

                    // Remove items from cart
                    cartService.deleteProductFromCart(cart.getCartId(),
                            item.getProduct()
                                    .getProductId());
                });

        OrderDTO orderDTO = modelMapper.map(savedOrder,
                OrderDTO.class);
        orderItems.forEach(item -> orderDTO.getOrderItems()
                .add(modelMapper.map(item,
                        OrderItemDTO.class)));

        orderDTO.setAddressId(addressId);

        return orderDTO;
    }
}
