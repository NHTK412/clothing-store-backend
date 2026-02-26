package com.example.clothingstore.exception;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.clothingstore.exception.customer.*;
import com.example.clothingstore.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(BadRequestException.class)
        public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException ex,
                        HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                // .body(new ApiResponse<>(false, ex.getMessage(), null));
                                .body(ApiResponse.error(HttpStatus.BAD_REQUEST, ex.getMessage(), null,
                                                request.getRequestURI()));
        }

        @ExceptionHandler(NotFoundException.class)
        public ResponseEntity<ApiResponse<Object>> handleNotFoundException(NotFoundException ex,
                        HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.error(HttpStatus.NOT_FOUND, ex.getMessage(), null,
                                                request.getRequestURI()));
        }

        @ExceptionHandler(ConflictException.class)
        public ResponseEntity<ApiResponse<Object>> handleConflictException(ConflictException ex,
                        HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.CONFLICT)
                                .body(ApiResponse.error(HttpStatus.CONFLICT, ex.getMessage(), null,
                                                request.getRequestURI()));
        }

        @ExceptionHandler(ServerErrorException.class)
        public ResponseEntity<ApiResponse<Object>> handleServerErrorException(Exception ex,
                        HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(), null,
                                                request.getRequestURI()));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(AccessDeniedException ex,
                        HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.error(HttpStatus.FORBIDDEN, ex.getMessage(), null,
                                                request.getRequestURI()));
        }

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(
                        MethodArgumentNotValidException ex, HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.error(HttpStatus.BAD_REQUEST,
                                                "Validation error: " + ex.getBindingResult().getFieldError()
                                                                .getDefaultMessage(),
                                                null,
                                                request.getRequestURI()));
        }

        // @ExceptionHandler(Exception.class)
        // public ResponseEntity<ApiResponse<Object>> handleGeneralException(Exception
        // ex) {
        // return ResponseEntity
        // .status(HttpStatus.INTERNAL_SERVER_ERROR)
        // .body(new ApiResponse<>(false, "An unexpected error occurred: " +
        // ex.getMessage(), null));
        // }

        @ExceptionHandler(IOException.class)
        public ResponseEntity<ApiResponse<Object>> handleIOException(IOException ex, HttpServletRequest request) {
                return ResponseEntity
                                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR,
                                                "File I/O error: " + ex.getMessage(), null,
                                                request.getRequestURI()));
        }

}
