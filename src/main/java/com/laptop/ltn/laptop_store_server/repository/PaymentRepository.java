package com.laptop.ltn.laptop_store_server.repository;

import com.laptop.ltn.laptop_store_server.entity.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends MongoRepository<Payment, String> {
    Payment findByOrderId(String orderId);
}
