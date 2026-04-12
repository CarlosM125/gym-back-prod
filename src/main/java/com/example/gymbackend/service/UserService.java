package com.example.gymbackend.service;

import com.example.gymbackend.payload.dto.UserDTO;
import java.util.List;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    List<UserDTO> getAllUsers();
    UserDTO getUserByDocumentId(String documentId);
}
