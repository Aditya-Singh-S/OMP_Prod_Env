package com.cts.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.cts.dto.ProductViewDTO;
import com.cts.exception.InvalidProductException;
import com.cts.repository.ProductViewRepository;
import com.cts.service.SearchServiceImpl;
import com.cts.service.SearchValidationService;

@ExtendWith(MockitoExtension.class)
public class SearchServiceTest {

    @Mock
    private ProductViewRepository productViewRepository;

    @Mock
    private SearchValidationService searchValidationService;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void searchProductByName_ReturnsNestleProduct_WhenNameIsNestle() {
        // Arrange
        String searchName = "Nestle Product";
        ProductViewDTO nestleProduct = new ProductViewDTO(101, searchName, "Delicious Nestle treat", 4.2, 150);
        when(productViewRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(List.of(nestleProduct));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductByName(searchName);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals(searchName, actualProducts.get(0).getName());
        verify(searchValidationService).validateProductName(searchName);
    }

    @Test
    void searchProductByName_ReturnsDairyMilk_WhenNameIsDairyMilk() {
        // Arrange
        String searchName = "Dairy Milk";
        ProductViewDTO dairyMilk = new ProductViewDTO(102, searchName, "Classic Dairy Milk chocolate bar", 4.8, 200);
        when(productViewRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(List.of(dairyMilk));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductByName(searchName);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals(searchName, actualProducts.get(0).getName());
        verify(searchValidationService).validateProductName(searchName);
    }

    @Test
    void searchProductByName_ThrowsException_WhenNameDoesNotExist() {
        // Arrange
        String searchName = "Mars";
        when(productViewRepository.findByNameContainingIgnoreCase(searchName)).thenReturn(List.of());

        // Act & Assert
        assertThrows(InvalidProductException.class, () -> searchService.searchProductByName(searchName));
        verify(searchValidationService).validateProductName(searchName);
    }

    @Test
    void searchProductBySubsCount_ReturnsMilkyBar_WhenCountMatchesMilkyBar() {
        // Arrange
        int searchCount = 180;
        ProductViewDTO milkyBar = new ProductViewDTO(103, "Milky Bar", "Smooth and creamy Milky Bar", 4.5, searchCount);
        when(productViewRepository.searchBySubsCount(searchCount)).thenReturn(List.of(milkyBar));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductBySubsCount(searchCount);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals("Milky Bar", actualProducts.get(0).getName());
        assertEquals(searchCount, actualProducts.get(0).getSubscription_count());
        verify(searchValidationService).validateSubsCount(searchCount);
    }

    @Test
    void searchProductByRating_ReturnsDairyMilk_WhenRatingMatchesDairyMilk() {
        // Arrange
        double searchRating = 4.8;
        ProductViewDTO dairyMilk = new ProductViewDTO(102, "Dairy Milk", "Classic Dairy Milk chocolate bar", searchRating, 200);
        when(productViewRepository.searchProductByRating(searchRating)).thenReturn(List.of(dairyMilk));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductByRating(searchRating);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals("Dairy Milk", actualProducts.get(0).getName());
        assertEquals(searchRating, actualProducts.get(0).getAvg_rating());
        verify(searchValidationService).validateRating(searchRating);
    }

    @Test
    void searchProductBySubsCountAndRating_ReturnsNestle_WhenCriteriaMatchNestle() {
        // Arrange
        int searchCount = 150;
        double searchRating = 4.2;
        ProductViewDTO nestle = new ProductViewDTO(101, "Nestle Product", "Delicious Nestle treat", searchRating, searchCount);
        when(productViewRepository.searchProductBySubsCountAndRating(searchCount, searchRating)).thenReturn(List.of(nestle));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductBySubsCountAndRating(searchCount, searchRating);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals("Nestle Product", actualProducts.get(0).getName());
        assertEquals(searchCount, actualProducts.get(0).getSubscription_count());
        assertEquals(searchRating, actualProducts.get(0).getAvg_rating());
        verify(searchValidationService).validateSearchCriteria(searchCount, searchRating);
    }

    @Test
    void searchProductByNameSubsRating_ReturnsMilkyBar_WhenAllCriteriaMatchMilkyBar() {
        // Arrange
        String searchName = "Milky Bar";
        int searchCount = 180;
        double searchRating = 4.5;
        ProductViewDTO milkyBar = new ProductViewDTO(103, searchName, "Smooth and creamy Milky Bar", searchRating, searchCount);
        when(productViewRepository.searchProductByNameSubsRating(searchName, searchCount, searchRating)).thenReturn(List.of(milkyBar));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductByNameSubsRating(searchName, searchCount, searchRating);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals(searchName, actualProducts.get(0).getName());
        assertEquals(searchCount, actualProducts.get(0).getSubscription_count());
        assertEquals(searchRating, actualProducts.get(0).getAvg_rating());
        verify(searchValidationService).validateSearchCriteria(searchName, searchCount, searchRating);
    }

    @Test
    void searchProductByNameAndRating_ReturnsDairyMilk_WhenNameAndRatingMatch() {
        // Arrange
        String searchName = "Dairy Milk";
        double searchRating = 4.8;
        ProductViewDTO dairyMilk = new ProductViewDTO(102, searchName, "Classic Dairy Milk chocolate bar", searchRating, 200);
        when(productViewRepository.searchProductByNameAndRating(searchName, searchRating)).thenReturn(List.of(dairyMilk));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductByNameAndRating(searchName, searchRating);

        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals(searchName, actualProducts.get(0).getName());
        assertEquals(searchRating, actualProducts.get(0).getAvg_rating());
        verify(searchValidationService).validateSearchCriteria(searchName, searchRating);
    }

    @Test
    void searchProductByNameAndSubsCount_ReturnsNestle_WhenNameAndSubsCountMatch() {
        // Arrange
        String searchName = "Nestle Product";
        int searchCount = 150;
        ProductViewDTO nestle = new ProductViewDTO(101, searchName, "Delicious Nestle treat", 4.2, searchCount);
        when(productViewRepository.searchProductByNameAndSubsCount(searchName, searchCount)).thenReturn(List.of(nestle));

        // Act
        List<ProductViewDTO> actualProducts = searchService.searchProductByNameAndSubsCount(searchName, searchCount);
        
        // Assert
        assertEquals(1, actualProducts.size());
        assertEquals(searchName, actualProducts.get(0).getName());
        assertEquals(searchCount, actualProducts.get(0).getSubscription_count());
        verify(searchValidationService).validateSearchCriteria(searchName, searchCount);
    }
}