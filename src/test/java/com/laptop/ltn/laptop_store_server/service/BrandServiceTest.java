package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Brand;
import com.laptop.ltn.laptop_store_server.repository.BrandRepository;
import com.laptop.ltn.laptop_store_server.service.impl.BrandServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @Mock
    private BrandRepository brandRepository;

    @InjectMocks
    private BrandServiceImpl brandService;

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
    @DisplayName("TCS-021: Get all brands should return all brands")
    void TCS021_getAllBrands_ShouldReturnAllBrands() {
        // Arrange
        List<Brand> expectedBrands = Arrays.asList(testBrand);
        when(brandRepository.findAll()).thenReturn(expectedBrands);

        // Act
        List<Brand> result = brandService.getAllBrands();

        // Assert
        assertEquals(expectedBrands, result);
        verify(brandRepository).findAll();
    }

    @Test
    @DisplayName("TCS-022: Create brand should set timestamps and save")
    void TCS022_createBrand_ShouldSetTimestampsAndSave() {
        // Arrange
        Brand newBrand = new Brand();
        newBrand.setTitle("New Brand");
        newBrand.setSlug("new-brand");
        when(brandRepository.save(any(Brand.class))).thenReturn(testBrand);

        // Act
        Brand result = brandService.createBrand(newBrand);

        // Assert
        assertNotNull(result.getCreatedAt());
        assertNotNull(result.getUpdatedAt());
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    @DisplayName("TCS-023: Update brand when brand exists should update and return brand")
    void TCS023_updateBrand_WhenBrandExists_ShouldUpdateAndReturnBrand() {
        // Arrange
        Brand updatedBrand = new Brand();
        updatedBrand.setTitle("Updated Brand");
        when(brandRepository.findById("1")).thenReturn(Optional.of(testBrand));
        when(brandRepository.save(any(Brand.class))).thenReturn(updatedBrand);

        // Act
        Optional<Brand> result = brandService.updateBrand("1", updatedBrand);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(updatedBrand.getTitle(), result.get().getTitle());
        verify(brandRepository).findById("1");
        verify(brandRepository).save(any(Brand.class));
    }

    @Test
    @DisplayName("TCS-024: Update brand when brand does not exist should return empty")
    void TCS024_updateBrand_WhenBrandDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        Brand updatedBrand = new Brand();
        when(brandRepository.findById("2")).thenReturn(Optional.empty());

        // Act
        Optional<Brand> result = brandService.updateBrand("2", updatedBrand);

        // Assert
        assertFalse(result.isPresent());
        verify(brandRepository).findById("2");
        verify(brandRepository, never()).save(any(Brand.class));
    }

    @Test
    @DisplayName("TCS-025: Delete brand when brand exists should return true")
    void TCS025_deleteBrand_WhenBrandExists_ShouldReturnTrue() {
        // Arrange
        when(brandRepository.existsById("1")).thenReturn(true);

        // Act
        boolean result = brandService.deleteBrand("1");

        // Assert
        assertTrue(result);
        verify(brandRepository).existsById("1");
        verify(brandRepository).deleteById("1");
    }

    @Test
    @DisplayName("TCS-026: Delete brand when brand does not exist should return false")
    void TCS026_deleteBrand_WhenBrandDoesNotExist_ShouldReturnFalse() {
        // Arrange
        when(brandRepository.existsById("2")).thenReturn(false);

        // Act
        boolean result = brandService.deleteBrand("2");

        // Assert
        assertFalse(result);
        verify(brandRepository).existsById("2");
        verify(brandRepository, never()).deleteById(anyString());
    }
}
