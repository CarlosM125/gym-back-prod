package com.example.gymbackend.payload.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CustomerDTO {
    private Long id;
    private String fullName;
    private String documentId;
    private String email;
    private String phone;
    private Integer pinZkteco;
    private String profileImageUrl;
    private String status;
    private Long homeBranchId;
    private String homeBranchName;
    private LocalDateTime createdAt;
    // Optional: for registering with initial plan
    private Long initialPlanId;
}
