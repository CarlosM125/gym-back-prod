package com.example.gymbackend.service;

import com.example.gymbackend.payload.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> getAllUsers();
    UserDTO createUser(UserDTO dto);
    void deleteUser(Long id);
}
