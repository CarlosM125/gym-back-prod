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

    List<MembershipTransaction> findByCustomerId(Long customerId);

    @Query(value = "SELECT p.name as planName, COUNT(DISTINCT t.customer_id) as clients, SUM(t.amount_paid) as revenue " +
            "FROM membership_transactions t " +
            "JOIN membership_plans p ON t.plan_id = p.id " +
            "GROUP BY p.name " +
            "ORDER BY revenue DESC", nativeQuery = true)
    List<Object[]> findRevenueByPlan();

    @Query("SELECT t FROM MembershipTransaction t WHERE " +
           "(:branchId IS NULL OR t.branch.id = :branchId) AND " +
           "(:planId IS NULL OR t.plan.id = :planId) AND " +
           "(cast(:startDate as timestamp) IS NULL OR t.transactionDate >= :startDate) AND " +
           "(cast(:endDate as timestamp) IS NULL OR t.transactionDate <= :endDate)")
    List<MembershipTransaction> findFilteredTransactions(
            @Param("branchId") Long branchId, 
            @Param("planId") Long planId, 
            @Param("startDate") java.time.LocalDateTime startDate, 
            @Param("endDate") java.time.LocalDateTime endDate);
}
