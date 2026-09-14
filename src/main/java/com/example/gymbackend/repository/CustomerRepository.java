package com.example.gymbackend.repository;

import com.example.gymbackend.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByDocumentId(String documentId);
    Optional<Customer> findByPinZkteco(Integer pinZkteco);

    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT c FROM Customer c LEFT JOIN Membership m ON m.customer = c AND m.status = 'ACTIVE' " +
           "WHERE c.status != 'DELETED' AND " +
           "(:search IS NULL OR :search = '' OR LOWER(c.fullName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.documentId) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:filterStatus IS NULL OR :filterStatus = 'ALL' OR " +
           "(:filterStatus = 'ACTIVE' AND m.endDate >= CURRENT_DATE) OR " +
           "(:filterStatus = 'EXPIRED' AND (m.id IS NULL OR m.endDate < CURRENT_DATE)) OR " +
           "(:filterStatus = 'EXPIRING_TODAY' AND m.endDate = CURRENT_DATE))")
    org.springframework.data.domain.Page<Customer> searchCustomers(
        @org.springframework.data.repository.query.Param("search") String search, 
        @org.springframework.data.repository.query.Param("filterStatus") String filterStatus, 
        org.springframework.data.domain.Pageable pageable);
}
