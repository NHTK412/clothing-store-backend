package com.example.clothingstore.dto.orderdetail;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDetailRequestDTO {

    @NotNull(message = "Order ID is required")
    private Integer productDetailId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be a positive integer")
    private Integer quantity;

    // @PositiveOrZero(message = "Price must be zero or a positive value")
    // private Double price;

    // @PositiveOrZero(message = "Discount must be zero or a positive value")
    // private Double discount;

    // @PositiveOrZero(message = "Final price must be zero or a positive value")
    // private Double finalPrice;

    // @NotNull(message = "Is free flag is required")
    // private Boolean isFree;

}
