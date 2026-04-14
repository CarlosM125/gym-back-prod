package com.example.gymbackend.payload.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MembershipDTO {
    private Long id;
    private Long customerId;   // renamed from userId
    private Long branchId;
    private Long planId;
    private Double amountPaid;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
    private String customerFullName;  // renamed from userFullName
    private String documentId;
}
