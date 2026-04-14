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
    private final com.example.gymbackend.service.CloudinaryService cloudinaryService;

    @PostMapping("/upload-image")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> uploadImage(
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        try {
            String url = cloudinaryService.uploadImage(file);
            java.util.Map<String, String> response = new java.util.HashMap<>();
            response.put("url", url);
            return ResponseEntity.ok(ApiResponse.success(response, "Image uploaded successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Image upload failed: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(userService.getAllUsers(), "Users fetched"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(userService.createUser(dto), "User created"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success(null, "User deleted"));
    }
}
