package com.project.ecommerce.controller;

import com.project.ecommerce.payload.OrderDTO;
import com.project.ecommerce.payload.OrderRequestDTO;
import com.project.ecommerce.service.OrderService;
import com.project.ecommerce.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AuthUtil authUtil;

    @PostMapping("/order/users/payments/{paymentMethod}")
    public ResponseEntity<OrderDTO> orderProducts(@PathVariable String paymentMethod,
                                                  @RequestBody OrderRequestDTO orderRequestDTO) {
        String emailId = authUtil.loggedInEmail();
        OrderDTO orderDTO = orderService.placeOrder(emailId,
                orderRequestDTO.getAddressId(),
                paymentMethod,
                orderRequestDTO.getPgName(),
                orderRequestDTO.getPgPaymentId(),
                orderRequestDTO.getPgStatus(),
                orderRequestDTO.getPgResponseMessage());
        return new ResponseEntity<>(orderDTO,
                HttpStatus.CREATED);
    }
}
