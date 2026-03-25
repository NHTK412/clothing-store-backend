package com.example.clothingstore.exception.business;

// 409
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
