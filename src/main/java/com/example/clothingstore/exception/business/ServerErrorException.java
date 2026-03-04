package com.example.clothingstore.exception.business;

// 500
public class ServerErrorException extends RuntimeException {
    public ServerErrorException(String message) {
        super(message);
    }
}
