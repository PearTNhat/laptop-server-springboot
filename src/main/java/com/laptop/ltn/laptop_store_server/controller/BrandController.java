package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.entity.Brand;
import com.laptop.ltn.laptop_store_server.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand") // The context-path /api is already set in application.yaml
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        try {
            List<Brand> brands = brandService.getAllBrands();
            return ResponseEntity.ok(ApiResponse.<List<Brand>>builder()
                    .data(brands)
                    .success(true)
                    .message("Brands retrieved successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Brand>>builder()
                            .success(false)
                            .message("Error retrieving brands: " + e.getMessage())
                            .build());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Brand>> getBrandById(@PathVariable String id) {
        try {
            return brandService.getBrandById(id)
                    .map(brand -> ResponseEntity.ok(ApiResponse.<Brand>builder()
                            .data(brand)
                            .success(true)
                            .message("Brand retrieved successfully")
                            .build()))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<Brand>builder()
                                    .success(false)
                                    .message("Brand not found with id: " + id)
                                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Brand>builder()
                            .success(false)
                            .message("Error retrieving brand: " + e.getMessage())
                            .build());
        }
    }

    // Add this endpoint for compatibility with the api/series/brand/{id} path
    @GetMapping("/series-brand/{id}")
    public ResponseEntity<ApiResponse<Brand>> getBrandByIdForSeries(@PathVariable String id) {
        return getBrandById(id); // Reuse existing endpoint logic
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Brand>> createBrand(@RequestBody Brand brand) {
        try {
            Brand createdBrand = brandService.createBrand(brand);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<Brand>builder()
                            .data(createdBrand)
                            .success(true)
                            .message("Brand created successfully")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Brand>builder()
                            .success(false)
                            .message("Error creating brand: " + e.getMessage())
                            .build());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Brand>> updateBrand(@PathVariable String id, @RequestBody Brand brand) {
        try {
            return brandService.updateBrand(id, brand)
                    .map(updatedBrand -> ResponseEntity.ok(ApiResponse.<Brand>builder()
                            .data(updatedBrand)
                            .success(true)
                            .message("Brand updated successfully")
                            .build()))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.<Brand>builder()
                                    .success(false)
                                    .message("Brand not found with id: " + id)
                                    .build()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Brand>builder()
                            .success(false)
                            .message("Error updating brand: " + e.getMessage())
                            .build());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable String id) {
        try {
            if (brandService.deleteBrand(id)) {
                return ResponseEntity.ok(ApiResponse.<Void>builder()
                        .success(true)
                        .message("Brand deleted successfully")
                        .build());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.<Void>builder()
                                .success(false)
                                .message("Brand not found with id: " + id)
                                .build());
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Error deleting brand: " + e.getMessage())
                            .build());
        }
    }
}
