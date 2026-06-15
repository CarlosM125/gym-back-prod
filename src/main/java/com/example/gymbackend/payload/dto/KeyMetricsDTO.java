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
public class KeyMetricsDTO {
    // We will use List of Maps with "month" and "rate" for the line charts
    private List<Map<String, Object>> renewalRate;
    private List<Map<String, Object>> nonRenewalRate;
    private List<Map<String, Object>> newSignupsRate;
}
