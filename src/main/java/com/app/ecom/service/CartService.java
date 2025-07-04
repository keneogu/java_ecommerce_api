package com.app.ecom.service;

import com.app.ecom.dto.CartItemRequest;
import com.app.ecom.model.CartItem;
import com.app.ecom.model.Product;
import com.app.ecom.model.User;
import com.app.ecom.repository.CartItemRepository;
import com.app.ecom.repository.ProductRepository;
import com.app.ecom.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CartService {

  @Autowired
  UserRepository userRepository;

  @Autowired
  ProductRepository productRepository;

  @Autowired
  CartItemRepository cartItemRepository;

  public boolean addToCart(String userId, CartItemRequest request) {
//    productRepository
    Optional<Product> productOpt = productRepository.findById(request.getProductId());
    if (productOpt.isEmpty()) return false;

    Product product = productOpt.get();
    if(product.getStockQuantity() < request.getQuantity()) return false;

    Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));
    if (userOpt.isEmpty()) return false;

    User user = userOpt.get();

    CartItem existingCartItem = cartItemRepository.findByUserAndProduct(user, product);
      if (existingCartItem != null) {
        existingCartItem.setQuantity(existingCartItem.getQuantity() + request.getQuantity());
        existingCartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(existingCartItem.getQuantity())));
        cartItemRepository.save(existingCartItem);
      } else {
        CartItem cartItem = new CartItem();
        cartItem.setUser(user);
        cartItem.setProduct(product);
        cartItem.setQuantity(request.getQuantity());
        cartItem.setPrice(product.getPrice().multiply(BigDecimal.valueOf(request.getQuantity())));
        cartItemRepository.save(cartItem);
      }
      return true;
  }

  public boolean deleteItemFromCart(String userId, Long productId) {
    Optional<Product> productOpt = productRepository.findById(productId);

    Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));

    if (productOpt.isPresent() && userOpt.isPresent()) {
      cartItemRepository.deleteByUserAndProduct(userOpt.get(), productOpt.get());
      return true;
    }
    return false;
  }

  public List<CartItem> getCart(String userId) {
    Optional<User> userOpt = userRepository.findById(Long.valueOf(userId));

    if (userOpt.isPresent()){
      return userOpt.map(cartItemRepository::findByUser).orElseGet(List::of);
    }
    return List.of();
  }

  public void clearCart(String userId) {
    userRepository.findById(Long.valueOf(userId)).ifPresent(user ->
        cartItemRepository.deleteByUser(user));
  }
}
