package com.example.clothingstore.dto.product;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class CreatePreviewDTO {

    @NoArgsConstructor
    @AllArgsConstructor
    @Setter
    @Getter
    public static class CreatePreviewDetailsDTO {
        private Integer productDetailIds;
        private Integer quantity;
    }

    private List<CreatePreviewDetailsDTO> details;
    private List<Integer> promotionIds;


}
