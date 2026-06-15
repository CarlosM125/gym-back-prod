package com.example.gymbackend.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipAnalysisDTO {
    private String planName;
    private long activeCount;
    private long expiredCount;
    private long cancelledCount;
}
