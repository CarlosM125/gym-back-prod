package com.example.gymbackend.controller;

import com.example.gymbackend.model.MarketingProposal;
import com.example.gymbackend.payload.response.ApiResponse;
import com.example.gymbackend.service.MarketingAgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marketing")
@RequiredArgsConstructor
public class MarketingController {

    private final MarketingAgentService marketingAgentService;

    /**
     * Returns all marketing proposals ordered by most recent first.
     */
    @GetMapping("/proposals")
    public ResponseEntity<ApiResponse<List<MarketingProposal>>> getProposals() {
        List<MarketingProposal> proposals = marketingAgentService.getAllProposals();
        return ResponseEntity.ok(ApiResponse.success(proposals, "Propuestas de marketing obtenidas"));
    }

    /**
     * Manually triggers the Gemini AI analysis (for admin use and testing).
     */
    @PostMapping("/trigger")
    public ResponseEntity<ApiResponse<String>> triggerAnalysis() {
        try {
            String result = marketingAgentService.runDailyAnalysis();
            return ResponseEntity.ok(ApiResponse.success(result, "Análisis ejecutado"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error ejecutando análisis: " + e.getMessage()));
        }
    }
}
