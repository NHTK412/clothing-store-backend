package com.example.clothingstore.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.clothingstore.dto.fileupload.FileUploadResponseDTO;
import com.example.clothingstore.service.FileUploadService;
import com.example.clothingstore.util.ApiResponse;

import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("v1/file-upload")
@RequiredArgsConstructor
public class FileUploadController {

        @Autowired
        private FileUploadService fileUploadService;

        @PostMapping(value = "/cdn", consumes = "multipart/form-data")
        public ResponseEntity<ApiResponse<FileUploadResponseDTO>> uploadToCdn(@RequestParam("file") MultipartFile file, HttpServletRequest request)
                        throws IOException {
                FileUploadResponseDTO fileUploadResponseDTO = fileUploadService.uploadFileToCloudinary(file);
                return ResponseEntity.ok(ApiResponse.success("Upload successful", fileUploadResponseDTO, request.getRequestURI()));
        }

        @PostMapping(value = "/cdn/multiple", consumes = "multipart/form-data")
        public ResponseEntity<ApiResponse<List<FileUploadResponseDTO>>> uploadMultipleFilesToCdn(
                        @RequestParam("files") List<MultipartFile> files, HttpServletRequest request)
                        throws IOException {
                List<FileUploadResponseDTO> fileUploadResponseDTOs = fileUploadService
                                .uploadMultipleFilesToCloudinary(files);
                return ResponseEntity.ok(ApiResponse.success("Upload successful", fileUploadResponseDTOs, request.getRequestURI()));
        }
}
