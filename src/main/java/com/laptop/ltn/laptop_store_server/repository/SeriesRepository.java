package com.laptop.ltn.laptop_store_server.repository;

import com.laptop.ltn.laptop_store_server.entity.Series;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface SeriesRepository extends MongoRepository<Series, String> {
    /**
     * Find all series that belong to a specific brand
     *
     * @param brandId The ID of the brand as String
     * @return List of Series objects with the matching brand ID
     */
    @Query("{ 'brand' : ?0 }")
    List<Series> findByBrandId(ObjectId brandId);

    /**
     * Find series by brand using string ID
     * Will convert string to ObjectId
     */
    default List<Series> findByBrand(String brandId) {
        try {
            ObjectId objectId = new ObjectId(brandId);
            return findByBrandId(objectId);
        } catch (Exception e) {
            return java.util.Collections.emptyList();
        }
    }
}