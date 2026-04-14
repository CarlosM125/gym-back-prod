package com.example.gymbackend.payload.dto;

import com.example.gymbackend.model.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {
    private Long id;
    private String username;
    private String password; // write-only, for account creation
    private Role role;
    private String firstName;
    private String lastName;
    private Boolean isActive;
}
