package com.example.demo.controller;

import com.example.demo.controllers.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    @InjectMocks
    private OrderController orderController;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderRepository orderRepository;

    private User user;
    private Cart cart;
    private Item item;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("Description of item1");

        cart = new Cart();
        cart.setId(1L);
        cart.setItems(Arrays.asList(item));
        cart.setTotal(BigDecimal.valueOf(10.00));

        user = new User();
        user.setId(1L);
        user.setUsername("test");
        user.setPassword("testPassword");
        user.setCart(cart);
        cart.setUser(user);
    }

    @Test
    public void testSubmitOrder() {
        when(userRepository.findByUsername("test")).thenReturn(user);

        ResponseEntity<UserOrder> response = orderController.submit("test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserOrder order = response.getBody();
        assertEquals(user, order.getUser());
        assertEquals(cart.getItems(), order.getItems());
        assertEquals(cart.getTotal(), order.getTotal());
    }

    @Test
    public void testSubmitOrder_UserNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(null);

        ResponseEntity<UserOrder> response = orderController.submit("test");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetOrdersForUser() {
        UserOrder order = UserOrder.createFromCart(cart);
        List<UserOrder> orders = Arrays.asList(order);
        when(userRepository.findByUsername("test")).thenReturn(user);
        when(orderRepository.findByUser(user)).thenReturn(orders);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orders, response.getBody());
    }

    @Test
    public void testGetOrdersForUser_UserNotFound() {
        when(userRepository.findByUsername("test")).thenReturn(null);

        ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("test");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
