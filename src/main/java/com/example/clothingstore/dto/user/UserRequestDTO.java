package com.example.clothingstore.dto.user;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.GenderEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {



    private String fullName;  

    @Email(message = "Email should be valid")
    private String email;

    private GenderEnum gender;

    @Past(message = "Date must be in the past")
    private LocalDateTime date;

    private String phone;

    private String image;

}
