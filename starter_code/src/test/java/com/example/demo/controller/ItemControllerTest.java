package com.example.demo.controller;

import com.example.demo.controllers.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class ItemControllerTest {
    @InjectMocks
    private ItemController itemController;

    @Mock
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetItems() {
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("item1");
        item1.setPrice(BigDecimal.valueOf(10.00));
        item1.setDescription("Description of item1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("item2");
        item2.setPrice(BigDecimal.valueOf(20.00));
        item2.setDescription("Description of item2");

        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item2));

        ResponseEntity<List<Item>> response = itemController.getItems();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals(item1.getName(), response.getBody().get(0).getName());
        assertEquals(item2.getName(), response.getBody().get(1).getName());
    }

    @Test
    public void testGetItemById() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("Description of item1");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(item.getName(), response.getBody().getName());
    }

    @Test
    public void testGetItemById_NotFound() {
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void testGetItemsByName() {
        Item item = new Item();
        item.setId(1L);
        item.setName("item1");
        item.setPrice(BigDecimal.valueOf(10.00));
        item.setDescription("Description of item1");

        when(itemRepository.findByName("item1")).thenReturn(Arrays.asList(item));

        ResponseEntity<List<Item>> response = itemController.getItemsByName("item1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(item.getName(), response.getBody().get(0).getName());
    }

    @Test
    public void testGetItemsByName_NotFound() {
        when(itemRepository.findByName("item1")).thenReturn(null);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("item1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
