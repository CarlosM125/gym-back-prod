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
public class UserDTO {
    private Long id;
    private Long homeBranchId;
    private Integer pinZkteco;
    private String fullName;
    private String documentId;
    private String email;
    private LocalDateTime createdAt;
}
