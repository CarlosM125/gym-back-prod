package com.example.gymbackend.service.impl;

import com.example.gymbackend.model.*;
import com.example.gymbackend.payload.dto.MembershipDTO;
import com.example.gymbackend.payload.dto.MembershipPlanDTO;
import com.example.gymbackend.repository.*;
import com.example.gymbackend.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;
    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final MembershipPlanRepository planRepository;
    private final MembershipTransactionRepository transactionRepository;

    @Override
    public List<MembershipPlanDTO> getAllPlans() {
        return planRepository.findAll().stream().map(this::mapPlanToDTO).collect(Collectors.toList());
    }

    @Override
    public MembershipPlanDTO createPlan(MembershipPlanDTO dto) {
        MembershipPlan plan = new MembershipPlan();
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPriceAmount(dto.getPriceAmount());
        plan.setDurationDays(dto.getDurationDays());
        plan.setIsPromotion(dto.getIsPromotion());
        return mapPlanToDTO(planRepository.save(plan));
    }

    @Override
    public MembershipPlanDTO updatePlan(Long id, MembershipPlanDTO dto) {
        MembershipPlan plan = planRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));
        plan.setName(dto.getName());
        plan.setDescription(dto.getDescription());
        plan.setPriceAmount(dto.getPriceAmount());
        plan.setDurationDays(dto.getDurationDays());
        plan.setIsPromotion(dto.getIsPromotion());
        return mapPlanToDTO(planRepository.save(plan));
    }

    @Override
    @Transactional
    public MembershipDTO createOrRenewMembership(MembershipDTO dto) {
        Customer customer = customerRepository.findByDocumentId(dto.getDocumentId())
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + dto.getDocumentId()));

        Branch branch = branchRepository.findById(dto.getBranchId())
                .orElseThrow(() -> new IllegalArgumentException("Branch not found"));

        MembershipPlan plan = planRepository.findById(dto.getPlanId())
                .orElseThrow(() -> new IllegalArgumentException("Plan not found"));

        // Cancel existing active memberships
        List<Membership> existing = membershipRepository.findByCustomerId(customer.getId());
        for (Membership m : existing) {
            if ("ACTIVE".equals(m.getStatus())) {
                m.setStatus("CANCELLED");
                membershipRepository.save(m);
            }
        }

        Membership newMembership = new Membership();
        newMembership.setCustomer(customer);
        newMembership.setBranch(branch);
        newMembership.setStartDate(LocalDate.now());
        newMembership.setEndDate(LocalDate.now().plusDays(plan.getDurationDays()));
        newMembership.setStatus("ACTIVE");
        Membership saved = membershipRepository.save(newMembership);

        // Immutable financial transaction record
        MembershipTransaction tx = new MembershipTransaction();
        tx.setCustomer(customer);
        tx.setBranch(branch);
        tx.setPlan(plan);
        tx.setAmountPaid(plan.getPriceAmount());
        tx.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(tx);

        return mapToDTO(saved);
    }

    @Override
    public List<MembershipDTO> getExpiringToday() {
        return membershipRepository.findMembershipsExpiringToday(LocalDate.now())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getFinancialStatsByYear(int year) {
        List<Object[]> rawData = transactionRepository.findFinancialStatsByYear(year);
        return rawData.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", mapMonthName(((Number) row[0]).intValue()));
            map.put("revenue", ((Number) row[1]).doubleValue());
            map.put("signups", ((Number) row[2]).intValue());
            return map;
        }).collect(Collectors.toList());
    }

    private String mapMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return (month >= 1 && month <= 12) ? months[month - 1] : "Unknown";
    }

    private MembershipDTO mapToDTO(Membership m) {
        return MembershipDTO.builder()
                .id(m.getId())
                .customerId(m.getCustomer().getId())
                .branchId(m.getBranch().getId())
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .status(m.getStatus())
                .customerFullName(m.getCustomer().getFullName())
                .documentId(m.getCustomer().getDocumentId())
                .build();
    }

    private MembershipPlanDTO mapPlanToDTO(MembershipPlan plan) {
        return MembershipPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .priceAmount(plan.getPriceAmount())
                .durationDays(plan.getDurationDays())
                .isPromotion(plan.getIsPromotion())
                .build();
    }
}
