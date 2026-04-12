package com.example.gymbackend.service.impl;

import com.example.gymbackend.model.Branch;
import com.example.gymbackend.model.User;
import com.example.gymbackend.payload.dto.UserDTO;
import com.example.gymbackend.repository.BranchRepository;
import com.example.gymbackend.repository.UserRepository;
import com.example.gymbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final BranchRepository branchRepository;

    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if (userDTO.getDocumentId() != null && userRepository.findByDocumentId(userDTO.getDocumentId()).isPresent()) {
            throw new IllegalArgumentException("Document ID already exists");
        }

        User user = new User();
        user.setFullName(userDTO.getFullName());
        user.setDocumentId(userDTO.getDocumentId());
        user.setEmail(userDTO.getEmail());
        
        // Handle Auto PIN Generate for ZKTeco if not provided
        if (userDTO.getPinZkteco() == null) {
            user.setPinZkteco(generateUniquePin());
        } else {
            if (userRepository.findByPinZkteco(userDTO.getPinZkteco()).isPresent()) {
                throw new IllegalArgumentException("ZKTeco PIN already in use");
            }
            user.setPinZkteco(userDTO.getPinZkteco());
        }

        if (userDTO.getHomeBranchId() != null) {
            Branch branch = branchRepository.findById(userDTO.getHomeBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
            user.setHomeBranch(branch);
        }

        User saved = userRepository.save(user);
        return mapToDTO(saved);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public UserDTO getUserByDocumentId(String documentId) {
        User user = userRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new IllegalArgumentException("User not found via document id: " + documentId));
        return mapToDTO(user);
    }

    private Integer generateUniquePin() {
        Random random = new Random();
        int pin;
        do {
            pin = 1000 + random.nextInt(9000);
        } while (userRepository.findByPinZkteco(pin).isPresent());
        return pin;
    }

    private UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .homeBranchId(user.getHomeBranch() != null ? user.getHomeBranch().getId() : null)
                .pinZkteco(user.getPinZkteco())
                .fullName(user.getFullName())
                .documentId(user.getDocumentId())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
