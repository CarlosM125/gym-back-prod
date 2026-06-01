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
        plan.setDurationMonths(dto.getDurationMonths());
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
        plan.setDurationMonths(dto.getDurationMonths());
        plan.setIsPromotion(dto.getIsPromotion());
        return mapPlanToDTO(planRepository.save(plan));
    }

    @Override
    @Transactional
    public MembershipDTO createOrRenewMembership(MembershipDTO dto) {
        Customer customer = null;
        
        if (dto.getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomerId()).orElse(null);
        }
        
        if (customer == null && dto.getDocumentId() != null && !dto.getDocumentId().isEmpty()) {
            customer = customerRepository.findByDocumentId(dto.getDocumentId()).orElse(null);
        }
        
        if (customer == null) {
            customer = new Customer();
            customer.setFullName(dto.getCustomerFullName() != null && !dto.getCustomerFullName().isEmpty() ? dto.getCustomerFullName() : "Cliente Anónimo");
            customer.setDocumentId(dto.getDocumentId() != null && !dto.getDocumentId().isEmpty() ? dto.getDocumentId() : null);
            customer.setConsentGiven(dto.getConsentGiven() != null ? dto.getConsentGiven() : false);
            customer = customerRepository.save(customer);
        }

        Branch branch = null;
        if (dto.getBranchId() != null) {
            branch = branchRepository.findById(dto.getBranchId()).orElse(null);
        }

        MembershipPlan plan = null;
        if (dto.getPlanId() != null) {
            plan = planRepository.findById(dto.getPlanId()).orElse(null);
        }
        
        LocalDate txDate = dto.getStartDate() != null ? dto.getStartDate() : LocalDate.now();
        int durationMonths = (plan != null && plan.getDurationMonths() != null) ? plan.getDurationMonths() : 1;

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
        newMembership.setStartDate(txDate);
        newMembership.setEndDate(txDate.plusMonths(durationMonths));
        newMembership.setStatus("ACTIVE");
        Membership saved = membershipRepository.save(newMembership);

        // Immutable financial transaction record
        MembershipTransaction tx = new MembershipTransaction();
        tx.setCustomer(customer);
        tx.setBranch(branch);
        tx.setPlan(plan);
        tx.setAmountPaid(dto.getAmountPaid() != null ? dto.getAmountPaid() : (plan != null ? plan.getPriceAmount() : 0.0));
        tx.setTransactionDate(txDate.atStartOfDay());
        transactionRepository.save(tx);

        return mapToDTO(saved);
    }

    @Override
    @Transactional
    public MembershipDTO updateActiveMembershipStartDate(Long customerId, LocalDate newStartDate) {
        // Find the active membership for this customer
        Membership activeMembership = membershipRepository.findByCustomerId(customerId).stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No active membership found for customer"));

        // Find the plan from the transaction to know durationMonths
        // Since Membership entity doesn't store duration directly, we check the latest transaction
        MembershipTransaction tx = transactionRepository.findByCustomerId(customerId).stream()
                .max((t1, t2) -> t1.getTransactionDate().compareTo(t2.getTransactionDate()))
                .orElse(null);

        int durationMonths = (tx != null && tx.getPlan() != null && tx.getPlan().getDurationMonths() != null) 
                             ? tx.getPlan().getDurationMonths() 
                             : 1;

        activeMembership.setStartDate(newStartDate);
        activeMembership.setEndDate(newStartDate.plusMonths(durationMonths));

        return mapToDTO(membershipRepository.save(activeMembership));
    }

    @Override
    public List<MembershipDTO> getExpiringToday() {
        return membershipRepository.findMembershipsExpiringToday(LocalDate.now())
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<MembershipDTO> getExpiringBetween(LocalDate from, LocalDate to) {
        return membershipRepository.findMembershipsExpiringBetween(from, to)
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

    @Override
    public com.example.gymbackend.payload.dto.DashboardStatsDTO getDashboardStats() {
        LocalDate today = LocalDate.now();
        
        long activeCustomers = membershipRepository.findAll().stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()) && m.getEndDate() != null && !m.getEndDate().isBefore(today))
                .map(m -> m.getCustomer().getId())
                .distinct()
                .count();

        List<Object[]> revenueByPlanRaw = transactionRepository.findRevenueByPlan();
        
        double totalRevenue = 0;
        for (Object[] row : revenueByPlanRaw) {
            totalRevenue += ((Number) row[2]).doubleValue();
        }
        
        final double finalTotal = totalRevenue;
        List<com.example.gymbackend.payload.dto.PlanDistributionDTO> planDistribution = revenueByPlanRaw.stream().map(row -> {
            String name = (String) row[0];
            int clients = ((Number) row[1]).intValue();
            double revenue = ((Number) row[2]).doubleValue();
            // Evitar Locale ES problemas con comas
            String percentage = finalTotal > 0 ? String.format(java.util.Locale.US, "%.1f%%", (revenue / finalTotal) * 100) : "0.0%";
            return new com.example.gymbackend.payload.dto.PlanDistributionDTO(name, clients, revenue, percentage);
        }).collect(Collectors.toList());
        
        int currentYear = today.getYear();
        List<Object[]> financialStats = transactionRepository.findFinancialStatsByYear(currentYear);
        double monthlyRevenue = 0;
        int currentMonth = today.getMonthValue();
        for (Object[] row : financialStats) {
            if (((Number) row[0]).intValue() == currentMonth) {
                monthlyRevenue = ((Number) row[1]).doubleValue();
            }
        }
        
        long totalUniqueCustomers = customerRepository.count();
        double averagePerCustomer = totalUniqueCustomers > 0 ? totalRevenue / totalUniqueCustomers : 0;
        
        return com.example.gymbackend.payload.dto.DashboardStatsDTO.builder()
                .activeCustomers(activeCustomers)
                .totalRevenue(totalRevenue)
                .averageRevenuePerCustomer(averagePerCustomer)
                .monthlyRevenue(monthlyRevenue)
                .planDistribution(planDistribution)
                .build();
    }

    private String mapMonthName(int month) {
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        return (month >= 1 && month <= 12) ? months[month - 1] : "Unknown";
    }

    private MembershipDTO mapToDTO(Membership m) {
        return MembershipDTO.builder()
                .id(m.getId())
                .customerId(m.getCustomer() != null ? m.getCustomer().getId() : null)
                .branchId(m.getBranch() != null ? m.getBranch().getId() : null)
                .startDate(m.getStartDate())
                .endDate(m.getEndDate())
                .status(m.getStatus())
                .customerFullName(m.getCustomer() != null ? m.getCustomer().getFullName() : null)
                .documentId(m.getCustomer() != null ? m.getCustomer().getDocumentId() : null)
                .profileImageUrl(m.getCustomer() != null ? m.getCustomer().getProfileImageUrl() : null)
                .build();
    }

    private MembershipPlanDTO mapPlanToDTO(MembershipPlan plan) {
        return MembershipPlanDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .priceAmount(plan.getPriceAmount())
                .durationMonths(plan.getDurationMonths())
                .isPromotion(plan.getIsPromotion())
                .build();
    }
}
