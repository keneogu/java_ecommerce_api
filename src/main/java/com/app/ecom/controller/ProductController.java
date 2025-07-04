package com.app.ecom.controller;

import com.app.ecom.dto.ProductRequest;
import com.app.ecom.dto.ProductResponse;
import com.app.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  @Autowired
  private ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> createProduct(@RequestBody ProductRequest productRequest){
    return new ResponseEntity<ProductResponse>(productService.createProduct(productRequest), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Long id,
      @RequestBody ProductRequest productRequest){
    return new ResponseEntity<ProductResponse>(productService.updateProduct(id, productRequest),
        HttpStatus.OK);
  }

  @GetMapping
  public ResponseEntity<List<ProductResponse>> getAllProducts() {
    return ResponseEntity.ok(productService.fetchAllProducts());
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(
      @PathVariable Long id){
    boolean deleted = productService.deleteProduct(id);
    return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
  }

  @GetMapping("/search")
  public ResponseEntity<List<ProductResponse>> searchProducts(@RequestParam String keyword) {
    return ResponseEntity.ok(productService.searchProducts(keyword));
  }

}
