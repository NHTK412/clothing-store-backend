package com.example.clothingstore.mapper.mapstruct;

import java.lang.annotation.Target;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.example.clothingstore.dto.user.UserRequestDTO;
import com.example.clothingstore.dto.user.UserResponseDTO;
import com.example.clothingstore.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponseDTO toResponseDTO(User user);

    @Mapping(target = "userId", ignore = true)
    @Mapping(target = "userName", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "password", ignore = true)
    void updateModelFromDTO(UserRequestDTO userRequestDTO, @MappingTarget User user);

}
