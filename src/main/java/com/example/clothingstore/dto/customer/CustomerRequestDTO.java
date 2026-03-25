package com.example.clothingstore.dto.customer;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.GenderEnum;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CustomerRequestDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String userName;

    // private String password;

    // @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    // @NotNull(message = "Gender is required")
    private GenderEnum gender;

    // @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    // @NotNull(message = "Date is required")
    @Past(message = "Date must be in the past")
    private LocalDateTime date;

    // @NotBlank(message = "Phone is required")
    @Pattern(regexp = "^\\d{10}$|^0[0-9]{9}$", message = "Phone must be 10 digits starting with 0") // VD: 
    private String phone;

    // @NotBlank(message = "Image is required")
    private String image;

}
