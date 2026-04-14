package com.example.gymbackend.repository;

import com.example.gymbackend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByDocumentId(String documentId);
    Optional<Customer> findByPinZkteco(Integer pinZkteco);
}
