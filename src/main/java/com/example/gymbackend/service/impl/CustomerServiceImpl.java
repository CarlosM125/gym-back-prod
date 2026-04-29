package com.example.gymbackend.service.impl;

import com.example.gymbackend.model.Branch;
import com.example.gymbackend.model.Customer;
import com.example.gymbackend.payload.dto.CustomerDTO;
import com.example.gymbackend.repository.BranchRepository;
import com.example.gymbackend.repository.CustomerRepository;
import com.example.gymbackend.repository.MembershipRepository;
import com.example.gymbackend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final BranchRepository branchRepository;
    private final MembershipRepository membershipRepository;

    @Override
    public CustomerDTO registerCustomer(CustomerDTO dto) {
        if (dto.getDocumentId() != null && customerRepository.findByDocumentId(dto.getDocumentId()).isPresent()) {
            throw new IllegalArgumentException("Document ID already registered");
        }

        Customer customer = new Customer();
        customer.setFullName(dto.getFullName());
        customer.setDocumentId(dto.getDocumentId());
        customer.setEmail(dto.getEmail());
        customer.setPhone(dto.getPhone());
        customer.setProfileImageUrl(dto.getProfileImageUrl());
        customer.setStatus("ACTIVE");

        if (dto.getPinZkteco() == null) {
            customer.setPinZkteco(generateUniquePin());
        } else {
            customer.setPinZkteco(dto.getPinZkteco());
        }

        if (dto.getHomeBranchId() != null) {
            Branch branch = branchRepository.findById(dto.getHomeBranchId())
                    .orElseThrow(() -> new IllegalArgumentException("Branch not found"));
            customer.setHomeBranch(branch);
        }

        return mapToDTO(customerRepository.save(customer));
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public CustomerDTO getCustomerByDocumentId(String documentId) {
        Customer customer = customerRepository.findByDocumentId(documentId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + documentId));
        return mapToDTO(customer);
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO dto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found: " + id));
        
        if (dto.getDocumentId() != null && !dto.getDocumentId().equals(customer.getDocumentId())) {
            if (customerRepository.findByDocumentId(dto.getDocumentId()).isPresent()) {
                throw new IllegalArgumentException("Document ID already registered to another customer");
            }
            customer.setDocumentId(dto.getDocumentId());
        }
        
        if (dto.getFullName() != null && !dto.getFullName().isEmpty()) {
            customer.setFullName(dto.getFullName());
        }
        if (dto.getEmail() != null) {
            customer.setEmail(dto.getEmail());
        }
        
        return mapToDTO(customerRepository.save(customer));
    }

    private Integer generateUniquePin() {
        Random random = new Random();
        int pin;
        do {
            pin = 1000 + random.nextInt(9000);
        } while (customerRepository.findByPinZkteco(pin).isPresent());
        return pin;
    }

    private CustomerDTO mapToDTO(Customer c) {
        CustomerDTO.CustomerDTOBuilder builder = CustomerDTO.builder()
                .id(c.getId())
                .fullName(c.getFullName())
                .documentId(c.getDocumentId())
                .email(c.getEmail())
                .phone(c.getPhone())
                .pinZkteco(c.getPinZkteco())
                .profileImageUrl(c.getProfileImageUrl())
                .status(c.getStatus())
                .homeBranchId(c.getHomeBranch() != null ? c.getHomeBranch().getId() : null)
                .homeBranchName(c.getHomeBranch() != null ? c.getHomeBranch().getName() : null)
                .createdAt(c.getCreatedAt());

        // Attach active membership info if exists
        try {
            membershipRepository.findByCustomerId(c.getId()).stream()
                .filter(m -> "ACTIVE".equals(m.getStatus()))
                .findFirst()
                .ifPresent(activeMem -> {
                    boolean isExpired = activeMem.getEndDate() != null && activeMem.getEndDate().isBefore(LocalDate.now());
                    
                    builder.currentStartDate(activeMem.getStartDate());
                    builder.currentEndDate(activeMem.getEndDate());
                    
                    if (activeMem.getPlan() != null) {
                        builder.currentPlanName(activeMem.getPlan().getName());
                    }
                    
                    if (isExpired) {
                        builder.membershipStatus("EXPIRED");
                        // Optionally update DB to CANCELLED/EXPIRED if needed, but for now just DTO mapping
                    } else {
                        builder.membershipStatus("ACTIVE");
                    }
                });
        } catch(Exception ignored) {}

        return builder.build();
    }
}
