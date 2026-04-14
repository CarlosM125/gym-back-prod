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

    @Query(value = "SELECT EXTRACT(MONTH FROM start_date) as month, COUNT(*) as signups " +
            "FROM memberships " +
            "WHERE EXTRACT(YEAR FROM start_date) = :year " +
            "GROUP BY EXTRACT(MONTH FROM start_date) " +
            "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> findHistoricalSignupsByYear(@Param("year") int year);

    List<Membership> findByCustomerId(Long customerId);
}
