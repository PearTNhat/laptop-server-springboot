package com.laptop.ltn.laptop_store_server.service.impl;

import com.laptop.ltn.laptop_store_server.entity.Brand;
import com.laptop.ltn.laptop_store_server.repository.BrandRepository;
import com.laptop.ltn.laptop_store_server.service.BrandService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class BrandServiceImpl implements BrandService {
    BrandRepository brandRepository;

    @Override
    public List<Brand> getAllBrands() {
        return brandRepository.findAll();
    }

    @Override
    public Brand createBrand(Brand brand) {
        brand.setCreatedAt(LocalDateTime.now());
        brand.setUpdatedAt(LocalDateTime.now());
        return brandRepository.save(brand);
    }

    @Override
    public Optional<Brand> updateBrand(String id, Brand brand) {
        return brandRepository.findById(id).map(existingBrand -> {
            if (brand.getTitle() != null) {
                existingBrand.setTitle(brand.getTitle());
            }
            if (brand.getImage() != null) {
                existingBrand.setImage(brand.getImage());
            }
            if (brand.getSlug() != null) {
                existingBrand.setSlug(brand.getSlug());
            }
            existingBrand.setUpdatedAt(LocalDateTime.now());
            return brandRepository.save(existingBrand);
        });
    }

    @Override
    public boolean deleteBrand(String id) {
        if (brandRepository.existsById(id)) {
            brandRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
