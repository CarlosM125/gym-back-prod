package com.example.gymbackend.repository;

import com.example.gymbackend.model.MembershipTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MembershipTransactionRepository extends JpaRepository<MembershipTransaction, Long> {

    @Query(value = "SELECT EXTRACT(MONTH FROM transaction_date) as month, SUM(amount_paid) as revenue, COUNT(*) as signups " +
            "FROM membership_transactions " +
            "WHERE EXTRACT(YEAR FROM transaction_date) = :year " +
            "GROUP BY EXTRACT(MONTH FROM transaction_date) " +
            "ORDER BY month ASC", nativeQuery = true)
    List<Object[]> findFinancialStatsByYear(@Param("year") int year);
}
