package com.laptop.ltn.laptop_store_server.configuration;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {
    @Value("${cloudinary.apiKey}")
    private String apiKey;
    @Value("${cloudinary.apiSecret}")
    private String apiSecret;
    @Value("${cloudinary.cloudName}")
    private String cloudName;
    @Bean
    Cloudinary configKey() {
        Map config = new HashMap();
        config.put("cloud_name",cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);
        return new Cloudinary(config);
    }
}
