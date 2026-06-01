package com.example.gymbackend.service;

import com.example.gymbackend.payload.dto.MembershipDTO;
import com.example.gymbackend.payload.dto.MembershipPlanDTO;

import java.util.List;
import java.util.Map;

public interface MembershipService {
    List<MembershipPlanDTO> getAllPlans();
    MembershipPlanDTO createPlan(MembershipPlanDTO planDTO);
    MembershipPlanDTO updatePlan(Long id, MembershipPlanDTO planDTO);
    MembershipDTO createOrRenewMembership(MembershipDTO membershipDTO);
    MembershipDTO updateActiveMembershipStartDate(Long customerId, java.time.LocalDate newStartDate);
    List<MembershipDTO> getExpiringToday();
    List<MembershipDTO> getExpiringBetween(java.time.LocalDate from, java.time.LocalDate to);
    List<Map<String, Object>> getFinancialStatsByYear(int year);
    com.example.gymbackend.payload.dto.DashboardStatsDTO getDashboardStats();
}
