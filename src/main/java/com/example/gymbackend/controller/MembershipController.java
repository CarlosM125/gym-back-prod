package com.example.gymbackend.controller;

import com.example.gymbackend.payload.dto.MembershipDTO;
import com.example.gymbackend.payload.response.ApiResponse;
import com.example.gymbackend.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/memberships")
@RequiredArgsConstructor
public class MembershipController {

    private final MembershipService membershipService;

    @PostMapping("/renew")
    public ResponseEntity<ApiResponse<MembershipDTO>> renewMembership(@RequestBody MembershipDTO dto) {
        MembershipDTO saved = membershipService.createOrRenewMembership(dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(saved, "Membership successfully renewed for 30 days"));
    }

    @GetMapping("/expiring-today")
    public ResponseEntity<ApiResponse<List<MembershipDTO>>> getExpiringToday() {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getExpiringToday(), "Expiring memberships fetched successfully"));
    }

    @GetMapping("/historical-stats")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getHistoricalStats(@RequestParam(defaultValue = "2026") int year) {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getFinancialStatsByYear(year), "Historical statistics loaded"));
    }

    @GetMapping("/plans")
    public ResponseEntity<ApiResponse<List<com.example.gymbackend.payload.dto.MembershipPlanDTO>>> getPlans() {
        return ResponseEntity.ok(ApiResponse.success(membershipService.getAllPlans(), "Plans loaded"));
    }

    @PostMapping("/plans")
    public ResponseEntity<ApiResponse<com.example.gymbackend.payload.dto.MembershipPlanDTO>> createPlan(@RequestBody com.example.gymbackend.payload.dto.MembershipPlanDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(membershipService.createPlan(dto), "Plan created"));
    }
}
