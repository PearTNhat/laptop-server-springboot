package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Series;
import com.laptop.ltn.laptop_store_server.service.SeriesService;
import com.laptop.ltn.laptop_store_server.dto.response.ApiResponse;
import com.laptop.ltn.laptop_store_server.entity.Brand;
import com.laptop.ltn.laptop_store_server.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/series") // Changed from "/api/series" to "/series" since context-path is already /api
public class SeriesController {

    @Autowired
    private SeriesService seriesService;

    @GetMapping
    public List<Series> getAllSeries() {
        return seriesService.getAllSeries();
    }

    @GetMapping("/brand/{id}")
    public ResponseEntity<ApiResponse<List<Series>>> getSeriesByBrandId(@PathVariable String id) {
        try {
            // Validate the input
            if (id == null || id.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.<List<Series>>builder()
                                .success(false)
                                .message("Missing input: brand ID is required")
                                .build());
            }

            List<Series> seriesList = seriesService.getSeriesByBrandId(id);
            return ResponseEntity.ok(ApiResponse.<List<Series>>builder()
                    .data(seriesList)
                    .success(true)
                    .message("Series for brand retrieved successfully")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<List<Series>>builder()
                            .success(false)
                            .message("Error retrieving series: " + e.getMessage())
                            .build());
        }
    }
}
