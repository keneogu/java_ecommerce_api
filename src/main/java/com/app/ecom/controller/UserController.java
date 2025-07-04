package com.app.ecom.controller;

import com.app.ecom.dto.UserRequest;
import com.app.ecom.dto.UserResponse;
import com.app.ecom.service.UserService;
import com.app.ecom.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

  @Autowired
  UserService userService;

  @GetMapping
  public ResponseEntity<List<UserResponse>> getAllUsers(){
    //these are the two ways of using ResponseEntity to return.
//    return ResponseEntity.ok(userService.fetchAllUsers());
    return new ResponseEntity<>(userService.fetchAllUsers(), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUser(@PathVariable Long id){
    return userService.fetchUser(id)
        .map(ResponseEntity::ok)
        .orElseGet(() -> ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest){
    userService.addUser(userRequest);
    return ResponseEntity.ok("User added successfully");
  }

  @PutMapping("/{id}")
  public ResponseEntity<String> updateUser(@RequestBody UserRequest updatedUserRequest, @PathVariable Long id) {
    boolean updated = userService.updateUser(updatedUserRequest, id);
    if(updated) return ResponseEntity.ok("User updated successfully");
    return ResponseEntity.notFound().build();
  }
}
