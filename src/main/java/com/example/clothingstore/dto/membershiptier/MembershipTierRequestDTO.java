package com.example.clothingstore.dto.membershiptier;

import org.springframework.security.access.method.P;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTierRequestDTO {

    @NotNull(message = "Tier name is required")
    private String tierName;

    @NotNull(message = "Minimum spending is required")
    @PositiveOrZero(message = "Minimum spending must be positive")
    private Double minimumSpending;

    // @NotNull(message = "Discount rate is required")
    // @Positive(message = "Discount rate must be positive")
    // private Double discountRate;

    // private String description;

    private String color;
}
