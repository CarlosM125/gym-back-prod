package com.example.gymbackend.controller;

import com.example.gymbackend.payload.dto.UserDTO;
import com.example.gymbackend.payload.response.ApiResponse;
import com.example.gymbackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> registerUser(@RequestBody UserDTO userDTO) {
        UserDTO created = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(created, "User registered successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(), "Users fetched successfully"));
    }

    @GetMapping("/document/{docId}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByDocument(@PathVariable String docId) {
        return ResponseEntity.ok(ApiResponse.success(userService.getUserByDocumentId(docId), "User found by Document ID"));
    }
}
