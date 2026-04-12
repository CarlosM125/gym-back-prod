package com.example.gymbackend.service;

import com.example.gymbackend.payload.dto.BranchDTO;

import java.util.List;

public interface BranchService {
    List<BranchDTO> getAllBranches();
    BranchDTO createBranch(BranchDTO branchDTO);
}
