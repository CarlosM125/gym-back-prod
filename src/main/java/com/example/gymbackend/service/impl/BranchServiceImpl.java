package com.example.gymbackend.service.impl;

import com.example.gymbackend.model.Branch;
import com.example.gymbackend.payload.dto.BranchDTO;
import com.example.gymbackend.repository.BranchRepository;
import com.example.gymbackend.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BranchDTO createBranch(BranchDTO branchDTO) {
        if (branchRepository.findByNameIgnoreCase(branchDTO.getName()).isPresent()) {
            throw new IllegalArgumentException("A branch with this name already exists.");
        }

        Branch branch = new Branch();
        branch.setName(branchDTO.getName());
        if (branchDTO.getTimezone() != null) {
            branch.setTimezone(branchDTO.getTimezone());
        }

        Branch savedBranch = branchRepository.save(branch);
        return mapToDTO(savedBranch);
    }

    private BranchDTO mapToDTO(Branch branch) {
        return BranchDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .timezone(branch.getTimezone())
                .createdAt(branch.getCreatedAt())
                .build();
    }
}
