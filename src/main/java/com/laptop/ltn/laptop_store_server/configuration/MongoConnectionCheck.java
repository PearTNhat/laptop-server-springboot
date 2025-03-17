package com.laptop.ltn.laptop_store_server.configuration;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class MongoConnectionCheck
{
    @Bean
    CommandLineRunner checkMongoConnection(MongoTemplate mongoTemplate) {
        return args -> {
            try {
                mongoTemplate.getDb().getName();
                System.out.println("✅ Kết nối MongoDB thành công!");
            } catch (Exception e) {
                System.err.println("❌ Lỗi kết nối MongoDB: " + e.getMessage());
            }
        };
    }
}
