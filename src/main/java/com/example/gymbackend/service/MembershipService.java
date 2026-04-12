package com.example.gymbackend.service;

import com.example.gymbackend.payload.dto.MembershipDTO;
import com.example.gymbackend.payload.dto.MembershipPlanDTO;

import java.util.List;
import java.util.Map;

public interface MembershipService {
    List<MembershipPlanDTO> getAllPlans();
    MembershipPlanDTO createPlan(MembershipPlanDTO planDTO);
    MembershipDTO createOrRenewMembership(MembershipDTO membershipDTO);
    List<MembershipDTO> getExpiringToday();
    List<Map<String, Object>> getFinancialStatsByYear(int year);
}
