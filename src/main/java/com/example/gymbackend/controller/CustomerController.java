package com.example.gymbackend.controller;

import com.example.gymbackend.payload.dto.CustomerDTO;
import com.example.gymbackend.payload.response.ApiResponse;
import com.example.gymbackend.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerDTO>> registerCustomer(@RequestBody CustomerDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(customerService.registerCustomer(dto), "Customer registered successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CustomerDTO>>> getAllCustomers() {
        return ResponseEntity.ok(ApiResponse.success(customerService.getAllCustomers(), "Customers fetched"));
    }

    @GetMapping("/by-document/{documentId}")
    public ResponseEntity<ApiResponse<CustomerDTO>> getByDocument(@PathVariable String documentId) {
        return ResponseEntity.ok(ApiResponse.success(
                customerService.getCustomerByDocumentId(documentId), "Customer found"));
    }
}
