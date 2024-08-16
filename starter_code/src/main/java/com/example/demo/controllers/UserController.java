package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @GetMapping("/id/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        LOGGER.debug("Find User by Id: " + id);
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("/{username}")
    public ResponseEntity<User> findByUserName(@PathVariable String username) {
        LOGGER.debug("Find User by name: " + username);
        User user = userRepository.findByUsername(username);
        return user == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(user);
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest createUserRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            LOGGER.error("Validation errors: " + bindingResult.getAllErrors());
            return ResponseEntity.badRequest().body(bindingResult.getAllErrors());
        }

        User user = new User();
        user.setUsername(createUserRequest.getUsername());
        Cart cart = new Cart();
        cartRepository.save(cart);
        user.setCart(cart);
        if (createUserRequest.getPassword().length() < 7 ||
                !createUserRequest.getPassword().equals(createUserRequest.getConfirmPassword())) {
            LOGGER.error("Invalid Password when creating User with name: " + user.getUsername());
            return ResponseEntity.badRequest().body("Password must be at least 7 characters long and both passwords must match.");
        }

        user.setPassword(bCryptPasswordEncoder.encode(createUserRequest.getPassword()));
        userRepository.save(user);
        LOGGER.info("Created User with name: " + user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}
