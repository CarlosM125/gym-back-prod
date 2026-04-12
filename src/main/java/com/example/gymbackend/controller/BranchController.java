package com.example.gymbackend.controller;

import com.example.gymbackend.payload.dto.BranchDTO;
import com.example.gymbackend.payload.response.ApiResponse;
import com.example.gymbackend.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/branches")
@RequiredArgsConstructor
public class BranchController {

    private final BranchService branchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<BranchDTO>>> getAllBranches() {
        List<BranchDTO> branches = branchService.getAllBranches();
        return ResponseEntity.ok(ApiResponse.success(branches, "Branches retrieved successfully"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BranchDTO>> createBranch(@RequestBody BranchDTO branchDTO) {
        BranchDTO createdBranch = branchService.createBranch(branchDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(createdBranch, "Branch created successfully"));
    }
}
