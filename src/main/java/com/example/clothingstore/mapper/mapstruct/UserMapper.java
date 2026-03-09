package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;

import com.example.clothingstore.dto.user.UserResponseDTO;
import com.example.clothingstore.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

}
