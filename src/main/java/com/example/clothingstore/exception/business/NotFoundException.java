package com.example.clothingstore.exception.business;


// 404
public class NotFoundException extends RuntimeException {

    public NotFoundException(String message) {
        super(message);
    }
}
