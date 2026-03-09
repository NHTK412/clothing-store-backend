package com.example.clothingstore.service;

import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.user.UserRequestDTO;
import com.example.clothingstore.dto.user.UserResponseDTO;
import com.example.clothingstore.mapper.mapstruct.UserMapper;
import com.example.clothingstore.model.User;
import com.example.clothingstore.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    final private UserRepository userRepository;

    final private UserMapper userMapper;

    public UserResponseDTO getUserById(
            Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        return userMapper.toResponseDTO(user);
    }

    public UserResponseDTO updateUser(
            Integer userId,
            UserRequestDTO userRequestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));

        userMapper.updateModelFromDTO(userRequestDTO, user);

        User updatedUser = userRepository.save(user);

        return userMapper.toResponseDTO(updatedUser);
    }
}
