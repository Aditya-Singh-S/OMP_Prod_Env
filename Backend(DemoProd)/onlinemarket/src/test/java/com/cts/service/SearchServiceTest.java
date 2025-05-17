package com.cts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.dto.ProductViewDTO;
import com.cts.exception.InvalidProductException;
import com.cts.repository.ProductViewRepository;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private ProductViewRepository productViewRepository;

    @Mock
    private SearchValidationService searchValidationService;

    @InjectMocks
    private SearchServiceImpl searchService;

    private ProductViewDTO product1;
    private ProductViewDTO product2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        product1 = new ProductViewDTO(1, "Laptop", "High-performance laptop", "laptop.jpg", 4.5, 100);
        product2 = new ProductViewDTO(2, "Mouse", "Wireless mouse", "mouse.jpg", 4.0, 50);
    }

    @Test
    void testSearchProductByName_Found() {
        String name = "Laptop";
        List<ProductViewDTO> expectedProducts = Collections.singletonList(product1);
        when(productViewRepository.findByNameContainingIgnoreCase(name)).thenReturn(expectedProducts);

        List<ProductViewDTO> actualProducts = searchService.searchProductByName(name);

        assertEquals(expectedProducts, actualProducts);
        verify(searchValidationService, times(1)).validateProductName(name);
        verify(productViewRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void testSearchProductByName_NotFound() {
        String name = "Keyboard";
        when(productViewRepository.findByNameContainingIgnoreCase(name)).thenReturn(Collections.emptyList());

        assertThrows(InvalidProductException.class, () -> searchService.searchProductByName(name));
        verify(searchValidationService, times(1)).validateProductName(name);
        verify(productViewRepository, times(1)).findByNameContainingIgnoreCase(name);
    }

    @Test
    void testSearchProductBySubsCount_Found() {
        int count = 100;
        List<ProductViewDTO> expectedProducts = Collections.singletonList(product1);
        when(productViewRepository.searchBySubsCount(count)).thenReturn(expectedProducts);

        List<ProductViewDTO> actualProducts = searchService.searchProductBySubsCount(count);

        assertEquals(expectedProducts, actualProducts);
        verify(searchValidationService, times(1)).validateSubsCount(count);
        verify(productViewRepository, times(1)).searchBySubsCount(count);
    }

    @Test
    void testSearchProductBySubsCount_NotFound() {
        int count = 200;
        when(productViewRepository.searchBySubsCount(count)).thenReturn(Collections.emptyList());

        List<ProductViewDTO> actualProducts = searchService.searchProductBySubsCount(count);

        assertEquals(Collections.emptyList(), actualProducts);
        verify(searchValidationService, times(1)).validateSubsCount(count);
        verify(productViewRepository, times(1)).searchBySubsCount(count);
    }

    @Test
    void testSearchProductByRating_Found() {
        double rating = 4.5;
        List<ProductViewDTO> expectedProducts = Collections.singletonList(product1);
        when(productViewRepository.searchProductByRating(rating)).thenReturn(expectedProducts);

        List<ProductViewDTO> actualProducts = searchService.searchProductByRating(rating);

        assertEquals(expectedProducts, actualProducts);
        verify(searchValidationService, times(1)).validateRating(rating);
        verify(productViewRepository, times(1)).searchProductByRating(rating);
    }

    @Test
    void testSearchProductByRating_NotFound() {
        double rating = 5.0;
        when(productViewRepository.searchProductByRating(rating)).thenReturn(Collections.emptyList());

        List<ProductViewDTO> actualProducts = searchService.searchProductByRating(rating);

        assertEquals(Collections.emptyList(), actualProducts);
        verify(searchValidationService, times(1)).validateRating(rating);
        verify(productViewRepository, times(1)).searchProductByRating(rating);
    }

    @Test
    void testSearchProductBySubsCountAndRating_Found() {
        int count = 100;
        double rating = 4.5;
        List<ProductViewDTO> expectedProducts = Collections.singletonList(product1);
        when(productViewRepository.searchProductBySubsCountAndRating(count, rating)).thenReturn(expectedProducts);

        List<ProductViewDTO> actualProducts = searchService.searchProductBySubsCountAndRating(count, rating);

        assertEquals(expectedProducts, actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(count, rating);
        verify(productViewRepository, times(1)).searchProductBySubsCountAndRating(count, rating);
    }

    @Test
    void testSearchProductBySubsCountAndRating_NotFound() {
        int count = 50;
        double rating = 4.5;
        when(productViewRepository.searchProductBySubsCountAndRating(count, rating)).thenReturn(Collections.emptyList());

        List<ProductViewDTO> actualProducts = searchService.searchProductBySubsCountAndRating(count, rating);

        assertEquals(Collections.emptyList(), actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(count, rating);
        verify(productViewRepository, times(1)).searchProductBySubsCountAndRating(count, rating);
    }

    @Test
    void testSearchProductByNameSubsRating_Found() {
        String name = "Laptop";
        int count = 100;
        double rating = 4.5;
        List<ProductViewDTO> expectedProducts = Collections.singletonList(product1);
        when(productViewRepository.searchProductByNameSubsRating(name, count, rating)).thenReturn(expectedProducts);

        List<ProductViewDTO> actualProducts = searchService.searchProductByNameSubsRating(name, count, rating);

        assertEquals(expectedProducts, actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(name, count, rating);
        verify(productViewRepository, times(1)).searchProductByNameSubsRating(name, count, rating);
    }

    @Test
    void testSearchProductByNameSubsRating_NotFound() {
        String name = "Laptop";
        int count = 50;
        double rating = 4.0;
        when(productViewRepository.searchProductByNameSubsRating(name, count, rating)).thenReturn(Collections.emptyList());

        List<ProductViewDTO> actualProducts = searchService.searchProductByNameSubsRating(name, count, rating);

        assertEquals(Collections.emptyList(), actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(name, count, rating);
        verify(productViewRepository, times(1)).searchProductByNameSubsRating(name, count, rating);
    }

    @Test
    void testSearchProductByNameAndRating_Found() {
        String name = "Laptop";
        double rating = 4.5;
        List<ProductViewDTO> expectedProducts = Collections.singletonList(product1);
        when(productViewRepository.searchProductByNameAndRating(name, rating)).thenReturn(expectedProducts);

        List<ProductViewDTO> actualProducts = searchService.searchProductByNameAndRating(name, rating);

        assertEquals(expectedProducts, actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(name, rating);
        verify(productViewRepository, times(1)).searchProductByNameAndRating(name, rating);
    }

    @Test
    void testSearchProductByNameAndRating_NotFound() {
        String name = "Laptop";
        double rating = 4.0;
        when(productViewRepository.searchProductByNameAndRating(name, rating)).thenReturn(Collections.emptyList());

        List<ProductViewDTO> actualProducts = searchService.searchProductByNameAndRating(name, rating);

        assertEquals(Collections.emptyList(), actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(name, rating);
        verify(productViewRepository, times(1)).searchProductByNameAndRating(name, rating);
    }

    @Test
    void testSearchProductByNameAndSubsCount_Found() {
        String name = "Laptop";
        int count = 100;
        List<ProductViewDTO> expectedProducts = Collections.singletonList(product1);
        when(productViewRepository.searchProductByNameAndSubsCount(name, count)).thenReturn(expectedProducts);

        List<ProductViewDTO> actualProducts = searchService.searchProductByNameAndSubsCount(name, count);

        assertEquals(expectedProducts, actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(name, count);
        verify(productViewRepository, times(1)).searchProductByNameAndSubsCount(name, count);
    }

    @Test
    void testSearchProductByNameAndSubsCount_NotFound() {
        String name = "Laptop";
        int count = 50;
        when(productViewRepository.searchProductByNameAndSubsCount(name, count)).thenReturn(Collections.emptyList());

        List<ProductViewDTO> actualProducts = searchService.searchProductByNameAndSubsCount(name, count);

        assertEquals(Collections.emptyList(), actualProducts);
        verify(searchValidationService, times(1)).validateSearchCriteria(name, count);
        verify(productViewRepository, times(1)).searchProductByNameAndSubsCount(name, count);
    }
}