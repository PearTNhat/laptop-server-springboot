package com.laptop.ltn.laptop_store_server;

import com.laptop.ltn.laptop_store_server.entity.Item;
import com.laptop.ltn.laptop_store_server.entity.User;
import com.laptop.ltn.laptop_store_server.repository.ItemRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication()
public class LaptopStoreServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(LaptopStoreServerApplication.class, args);
    }

}
