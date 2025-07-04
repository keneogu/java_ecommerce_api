package com.app.ecom.service;

import com.app.ecom.dto.OrderItemDTO;
import com.app.ecom.dto.OrderResponse;
import com.app.ecom.model.*;
import com.app.ecom.repository.OrderRepository;
import com.app.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
  @Autowired
  CartService cartService;

  @Autowired
  UserRepository userRepository;

  @Autowired
  OrderRepository orderRepository;

  public Optional<OrderResponse> createOrder(String userId) {
    List<CartItem> cartItems = cartService.getCart(userId);
    if (cartItems.isEmpty()){
      return Optional.empty();
    }

    Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
    if (userOpt.isEmpty()) {
      return Optional.empty();
    }
    User user = userOpt.get();

    BigDecimal totalPrice = cartItems.stream()
        .map(CartItem::getPrice)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    Order order = new Order();
    order.setUser(user);
    order.setStatus(OrderStatus.CONFIRMED);
    order.setTotalAmount(totalPrice);

    List<OrderItem> orderItems = cartItems.stream()
        .map(item -> new OrderItem(
            null,
            item.getProduct(),
            item.getQuantity(),
            item.getPrice(),
            order
        )).toList();

    order.setItems(orderItems);
    Order savedOrder = orderRepository.save(order);
    cartService.clearCart(userId);

    return Optional.of(mapToOrderResponse(savedOrder));
  }

  private OrderResponse mapToOrderResponse(Order order) {
    return new OrderResponse(
        order.getId(),
        order.getTotalAmount(),
        order.getStatus(),
        order.getItems().stream()
            .map(orderItem -> new OrderItemDTO(
                orderItem.getId(),
                orderItem.getProduct().getId(),
                orderItem.getQuantity(),
                orderItem.getPrice(),
                orderItem.getPrice().multiply(
                    new BigDecimal(orderItem.getQuantity())
                )
            )).toList(),
        order.getCreatedAt()
    );
  }
}
