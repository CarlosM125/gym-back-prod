package com.example.gymbackend.controller;

import com.example.gymbackend.model.Role;
import com.example.gymbackend.model.User;
import com.example.gymbackend.payload.response.ApiResponse;
import com.example.gymbackend.repository.UserRepository;
import com.example.gymbackend.security.JwtUtil;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();

        AuthResponse response = new AuthResponse(jwt, user.getRole().name(),
                user.getFirstName() + " " + user.getLastName());
        return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
    }

    @Data
    static class AuthRequest {
        private String username;
        private String password;
    }

    @Data
    static class AuthResponse {
        private final String token;
        private final String role;
        private final String fullName;
        public AuthResponse(String token, String role, String fullName) {
            this.token = token;
            this.role = role;
            this.fullName = fullName;
        }
    }
}
