package com.example.clothingstore.dto.membershiptier;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTierResponseDTO {

    private Integer tiedId;

    private String tierName;

    private Double minimumSpending;

    // private Double discountRate;

    // private String description;

    private String color;
}
