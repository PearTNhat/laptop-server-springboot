package com.laptop.ltn.laptop_store_server.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * Test configuration that explicitly enables component scanning for tests
 */
@TestConfiguration
@ComponentScan(basePackages = {
        "com.laptop.ltn.laptop_store_server.repository",
        "com.laptop.ltn.laptop_store_server.service",
        "com.laptop.ltn.laptop_store_server.utils"
})
public class TestConfig {
    // Any test-specific beans can be defined here if needed
}
