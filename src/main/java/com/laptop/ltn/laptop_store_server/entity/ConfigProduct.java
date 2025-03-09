package com.laptop.ltn.laptop_store_server.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@NoArgsConstructor
@Builder
public class ConfigProduct {
    private String value;
    private String description;
    private String name;
    private Integer priority;
    public ConfigProduct(String value, String description, String name, Integer priority) {
        this.value = value;
        this.description = description;
        this.name = name;
        this.priority = priority;
    }

    public ConfigProduct(String description, String name, Integer priority) {
        this.description = description;
        this.name = name;
        this.priority = priority;
    }
}
