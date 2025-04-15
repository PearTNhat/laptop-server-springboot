package com.laptop.ltn.laptop_store_server.service;

import com.laptop.ltn.laptop_store_server.entity.Brand;

import java.util.List;
import java.util.Optional;

public interface BrandService {
    List<Brand> getAllBrands();

    Brand createBrand(Brand brand);

    Optional<Brand> updateBrand(String id, Brand brand);

    boolean deleteBrand(String id);

    /**
     * Retrieves a brand by its ID
     *
     * @param id The ID of the brand to retrieve
     * @return An Optional containing the brand if found, or empty if not found
     */
    Optional<Brand> getBrandById(String id);
}
