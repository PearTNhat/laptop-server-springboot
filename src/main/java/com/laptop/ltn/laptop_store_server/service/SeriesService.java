package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Series;
import java.util.List;

public interface SeriesService {

    List<Series> getAllSeries();

    /**
     * Find all series that belong to a specific brand
     * 
     * @param brandId The ID of the brand
     * @return List of Series objects with the matching brand ID
     */
    List<Series> getSeriesByBrandId(String brandId);
}
