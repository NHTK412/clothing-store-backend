package com.example.clothingstore.dto.gift;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftRequestDTO {

    @NotNull(message = "Gift name is required")
    @Positive(message = "Gift quantity must be positive")
    private Integer giftQuantity; 

    @Positive(message = "Max gift must be positive")
    private Integer maxGift;

    @NotNull(message = "Product detail ID is required")
    private Integer productDetailId;
}
