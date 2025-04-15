package com.laptop.ltn.laptop_store_server.service.impl;

import com.laptop.ltn.laptop_store_server.entity.Series;
import com.laptop.ltn.laptop_store_server.repository.SeriesRepository;
import com.laptop.ltn.laptop_store_server.service.SeriesService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SeriesServiceImpl implements SeriesService {

    SeriesRepository seriesRepository;

    @Override
    public List<Series> getAllSeries() {
        return seriesRepository.findAll();
    }

    @Override
    public List<Series> getSeriesByBrandId(String brandId) {
        log.info("Searching for series with brandId: {}", brandId);

        // The repository will handle the conversion from String to ObjectId
        List<Series> result = seriesRepository.findByBrand(brandId);
        log.info("Found {} series with brand ID {}", result.size(), brandId);

        return result;
    }
}
