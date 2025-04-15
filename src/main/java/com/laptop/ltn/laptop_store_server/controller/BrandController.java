package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.entity.Brand;
import com.laptop.ltn.laptop_store_server.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        try {
            List<Brand> brands = brandService.getAllBrands();
            return ResponseEntity.ok(ApiResponse.<List<Brand>>builder()
                    .data(brands)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Brand>>builder()
                            .success(false)
                            .message("Error retrieving brands: " + e.getMessage())
                            .build());
        }
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Brand>> createBrand(@RequestBody Brand brand) {
        try {
            Brand createdBrand = brandService.createBrand(brand);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.<Brand>builder()
                            .data(createdBrand)
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
    public ResponseEntity<ApiResponse<Brand>> updateBrand(@PathVariable String id, @RequestBody Brand brand) {
        try {
            return brandService.updateBrand(id, brand)
                    .map(updatedBrand -> ResponseEntity.ok(ApiResponse.<Brand>builder()
                            .data(updatedBrand)
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
    public ResponseEntity<ApiResponse<Void>> deleteBrand(@PathVariable String id) {
        try {
            if (brandService.deleteBrand(id)) {
                return ResponseEntity.ok(ApiResponse.<Void>builder()
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
