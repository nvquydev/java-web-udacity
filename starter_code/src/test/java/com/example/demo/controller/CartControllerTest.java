package com.example.demo.controller;

import ch.qos.logback.core.AppenderBase;
import com.example.demo.controllers.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.event.LoggingEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class CartControllerTest extends AppenderBase<LoggingEvent> {
    static List<LoggingEvent> events = new ArrayList<>();
    @InjectMocks
    private CartController cartController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ItemRepository itemRepository;

    @Override
    protected void append(LoggingEvent loggingEvent) {
        events.add(loggingEvent);
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testAddToCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCart_ItemNotFound() {
        User user = new User();
        user.setUsername("testuser");
        Cart cart = new Cart();
        user.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testAddToCart_Success() {
        User user = new User();
        user.setUsername("testuser");
        Cart cart = new Cart();
        user.setCart(cart);

        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal("10.00"));

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(2);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.addTocart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().getItems().size());
        assertEquals(new BigDecimal("20.00"), response.getBody().getTotal());
    }

    @Test
    public void testRemoveFromCart_UserNotFound() {
        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(null);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCart_ItemNotFound() {
        User user = new User();
        user.setUsername("testuser");
        Cart cart = new Cart();
        user.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testRemoveFromCart_Success() {
        User user = new User();
        user.setUsername("testuser");
        Cart cart = new Cart();
        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal("10.00"));

        cart.addItem(item);
        user.setCart(cart);

        ModifyCartRequest request = new ModifyCartRequest();
        request.setUsername("testuser");
        request.setItemId(1L);
        request.setQuantity(1);

        when(userRepository.findByUsername("testuser")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        ResponseEntity<Cart> response = cartController.removeFromcart(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(0, response.getBody().getItems().size());
    }
}
