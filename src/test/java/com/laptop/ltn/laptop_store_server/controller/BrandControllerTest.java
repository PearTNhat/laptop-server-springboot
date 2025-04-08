package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.entity.Brand;
import com.laptop.ltn.laptop_store_server.service.BrandService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandControllerTest {

    @Mock
    private BrandService brandService;

    @InjectMocks
    private BrandController brandController;

    private Brand testBrand;

    @BeforeEach
    void setUp() {
        testBrand = new Brand();
        testBrand.set_id("1");
        testBrand.setTitle("Test Brand");
        testBrand.setSlug("test-brand");
        testBrand.setCreatedAt(LocalDateTime.now());
        testBrand.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @DisplayName("TCC-011: Get all brands should return brands")
    void TCC011_getAllBrands_ShouldReturnBrands() {
        // Arrange
        List<Brand> expectedBrands = Arrays.asList(testBrand);
        when(brandService.getAllBrands()).thenReturn(expectedBrands);

        // Act
        ResponseEntity<ApiResponse<List<Brand>>> response = brandController.getAllBrands();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals(expectedBrands, response.getBody().getData());
        verify(brandService).getAllBrands();
    }

    @Test
    @DisplayName("TCC-012: Create brand should return created brand")
    void TCC012_createBrand_ShouldReturnCreatedBrand() {
        // Arrange
        when(brandService.createBrand(any(Brand.class))).thenReturn(testBrand);

        // Act
        ResponseEntity<ApiResponse<Brand>> response = brandController.createBrand(testBrand);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals(testBrand, response.getBody().getData());
        assertEquals("Brand created successfully", response.getBody().getMessage());
        verify(brandService).createBrand(any(Brand.class));
    }

    @Test
    @DisplayName("TCC-013: Update brand when brand exists should return updated brand")
    void TCC013_updateBrand_WhenBrandExists_ShouldReturnUpdatedBrand() {
        // Arrange
        Brand updatedBrand = new Brand();
        updatedBrand.setTitle("Updated Brand");
        when(brandService.updateBrand("1", updatedBrand)).thenReturn(Optional.of(updatedBrand));

        // Act
        ResponseEntity<ApiResponse<Brand>> response = brandController.updateBrand("1", updatedBrand);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals(updatedBrand, response.getBody().getData());
        assertEquals("Brand updated successfully", response.getBody().getMessage());
        verify(brandService).updateBrand("1", updatedBrand);
    }

    @Test
    @DisplayName("TCC-014: Update brand when brand does not exist should return not found")
    void TCC014_updateBrand_WhenBrandDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        Brand updatedBrand = new Brand();
        when(brandService.updateBrand("2", updatedBrand)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<ApiResponse<Brand>> response = brandController.updateBrand("2", updatedBrand);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Brand not found with id: 2", response.getBody().getMessage());
        verify(brandService).updateBrand("2", updatedBrand);
    }

    @Test
    @DisplayName("TCC-015: Delete brand when brand exists should return success message")
    void TCC015_deleteBrand_WhenBrandExists_ShouldReturnSuccessMessage() {
        // Arrange
        when(brandService.deleteBrand("1")).thenReturn(true);

        // Act
        ResponseEntity<ApiResponse<Void>> response = brandController.deleteBrand("1");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().getSuccess());
        assertEquals("Brand deleted successfully", response.getBody().getMessage());
        verify(brandService).deleteBrand("1");
    }

    @Test
    @DisplayName("TCC-016: Delete brand when brand does not exist should return not found")
    void TCC016_deleteBrand_WhenBrandDoesNotExist_ShouldReturnNotFound() {
        // Arrange
        when(brandService.deleteBrand("2")).thenReturn(false);

        // Act
        ResponseEntity<ApiResponse<Void>> response = brandController.deleteBrand("2");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertFalse(response.getBody().getSuccess());
        assertEquals("Brand not found with id: 2", response.getBody().getMessage());
        verify(brandService).deleteBrand("2");
    }
}
