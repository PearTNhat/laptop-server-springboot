package com.laptop.ltn.laptop_store_server.service.impl;

import com.laptop.ltn.laptop_store_server.entity.Series;
import com.laptop.ltn.laptop_store_server.repository.SeriesRepository;
import com.laptop.ltn.laptop_store_server.service.SeriesService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
public class SeriesServiceImpl implements SeriesService {
    private final SeriesRepository seriesRepository;

    @Override
    public List<Series> getAllSeries() {
        List<Series> seriesList = seriesRepository.findAll();
        if (seriesList.isEmpty()) {
            throw new RuntimeException("Empty series");
        }
        return seriesList;
    }
}
