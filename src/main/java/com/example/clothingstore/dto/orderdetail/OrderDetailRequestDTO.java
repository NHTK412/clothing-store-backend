package com.example.clothingstore.dto.orderdetail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDTO {

    @NotNull(message = "Order ID is required")
    Integer productDetailId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive integer")
    Integer quantity;
}
