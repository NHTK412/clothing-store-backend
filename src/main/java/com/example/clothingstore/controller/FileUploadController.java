package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.clothingstore.dto.fileupload.FileUploadResponseDTO;
import com.example.clothingstore.service.FileUploadService;
import com.example.clothingstore.util.ApiResponse;

import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@RestController
@RequestMapping("file-upload")
@RequiredArgsConstructor

public class FileUploadController {

    // @Autowired
    // private FileUploadService fileUploadService;

    private final FileUploadService fileUploadService;

    @PreAuthorize("hasRole('ADMIN')")
    // Consumer để nói kiểu gửi lên
    @PostMapping(value = "/image", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<FileUploadResponseDTO>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            FileUploadResponseDTO fileUploadResponseDTO = fileUploadService.uploadImage(file);
            return ResponseEntity.ok(new ApiResponse<>(true, null, fileUploadResponseDTO));
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, "Unable to save file due to I/O error:" + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/multiple", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<List<FileUploadResponseDTO>>> uploadMultipleImage(
            @RequestParam("files") List<MultipartFile> files) {
        try {
            List<FileUploadResponseDTO> fileUploadResponseDTOs = fileUploadService.uploadMultipleImage(files);
            return ResponseEntity.ok(new ApiResponse<>(true, null, fileUploadResponseDTOs));
        } catch (IOException e) {
            return ResponseEntity.status(500)
                    .body(new ApiResponse<>(false, "Unable to save file due to I/O error:" + e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{fileName}")
    public ResponseEntity<ApiResponse<FileUploadResponseDTO>> deleteImage(@PathVariable String fileName) {
        FileUploadResponseDTO fileUploadResponseDTO = fileUploadService.deleteImage(fileName);
        return ResponseEntity.ok(new ApiResponse<>(true, null, fileUploadResponseDTO));
    }

}
