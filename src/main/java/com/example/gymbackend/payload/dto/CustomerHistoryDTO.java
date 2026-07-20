package com.example.gymbackend.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerHistoryDTO {
    private Long transactionId;
    private LocalDateTime transactionDate;
    private String planName;
    private Double amountPaid;
    private String branchName;
}
