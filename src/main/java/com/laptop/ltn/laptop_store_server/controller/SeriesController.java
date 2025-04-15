package com.laptop.ltn.laptop_store_server.controller;

import com.laptop.ltn.laptop_store_server.entity.Series;
import com.laptop.ltn.laptop_store_server.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/series")
@RequiredArgsConstructor
public class SeriesController {
    SeriesService seriesService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllSeries() {
        Map<String, Object> response = new HashMap<>();
        List<Series> seriesList = seriesService.getAllSeries();
        response.put("success", true);
        response.put("data", true);

        return ResponseEntity.ok(response);
    }
}
