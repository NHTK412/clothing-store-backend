package com.example.clothingstore.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    // private Integer code;

    private Boolean success;

    private String message;

    private T data;

}
