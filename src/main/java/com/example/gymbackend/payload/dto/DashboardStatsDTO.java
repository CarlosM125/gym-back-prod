package com.example.gymbackend.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private long activeCustomers;
    private double totalRevenue;
    private double averageRevenuePerCustomer;
    private double monthlyRevenue;
    private List<PlanDistributionDTO> planDistribution;
    private List<Map<String, Object>> historicalStats;
    private List<TopCustomerDTO> topCustomers;
    private KeyMetricsDTO keyMetrics;
    private List<MembershipAnalysisDTO> membershipAnalysis;
}
