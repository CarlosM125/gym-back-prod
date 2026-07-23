package com.example.gymbackend.repository;

import com.example.gymbackend.model.Membership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, Long> {

    @Query("SELECT m FROM Membership m WHERE m.endDate = :today AND m.status = 'ACTIVE'")
    List<Membership> findMembershipsExpiringToday(@Param("today") LocalDate today);

    @Query("SELECT m FROM Membership m WHERE m.endDate BETWEEN :fromDate AND :toDate AND m.status = 'ACTIVE' ORDER BY m.endDate ASC")
    List<Membership> findMembershipsExpiringBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);

    @Query(value = "SELECT EXTRACT(MONTH FROM start_date) as month, COUNT(*) as signups " +
            "FROM memberships " +
            "WHERE EXTRACT(YEAR FROM start_date) = :year " +
            "GROUP BY EXTRACT(MONTH FROM start_date) " +
            "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> findHistoricalSignupsByYear(@Param("year") int year);

    List<Membership> findByCustomerId(Long customerId);
    
    List<Membership> findByCustomerIdIn(List<Long> customerIds);

    @Query("SELECT COUNT(DISTINCT m.customer.id) FROM Membership m WHERE " +
           "(:status IS NULL OR m.status = :status) AND " +
           "(:status IS NOT NULL OR (m.status = 'ACTIVE' AND m.endDate >= :today)) AND " +
           "(:branchId IS NULL OR m.branch.id = :branchId)")
    long countActiveCustomers(
            @Param("status") String status, 
            @Param("today") LocalDate today, 
            @Param("branchId") Long branchId);
}
