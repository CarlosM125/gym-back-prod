package com.example.gymbackend.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PlanDistributionDTO {
    private String name;
    private int clients;
    private double revenue;
    private String percentage;
}
