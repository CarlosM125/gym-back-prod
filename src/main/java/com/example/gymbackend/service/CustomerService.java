package com.example.gymbackend.service;

import com.example.gymbackend.payload.dto.CustomerDTO;

import java.util.List;

public interface CustomerService {
    CustomerDTO registerCustomer(CustomerDTO dto);
    List<CustomerDTO> getAllCustomers();
    CustomerDTO getCustomerByDocumentId(String documentId);
    CustomerDTO updateCustomer(Long id, CustomerDTO dto);
}
