package com.example.clothingstore.dto.user;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.AccountStatusEnum;
import com.example.clothingstore.enums.GenderEnum;
import com.example.clothingstore.enums.RoleEnum;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserResponseDTO {

    private Integer userId;

    private String userName;

    private String email;

    private GenderEnum gender;

    private String fullName;

    private LocalDateTime date;

    private String phone;

    private String image;

    private AccountStatusEnum status;

    private RoleEnum role;

}
