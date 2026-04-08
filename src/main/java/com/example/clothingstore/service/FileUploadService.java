package com.example.clothingstore.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.clothingstore.dto.fileupload.FileUploadResponseDTO;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.model.UploadedFile;
import com.example.clothingstore.repository.UploadedFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FileUploadService {

    @Value("${image-upload-path}")
    private String imagePathString;

    private final Cloudinary cloudinary;

    private final UploadedFileRepository uploadedFileRepository;

    private final static Logger logger = org.slf4j.LoggerFactory.getLogger(FileUploadService.class);

    @Transactional
    public FileUploadResponseDTO uploadFileToCloudinary(MultipartFile file) throws IOException {

        var uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                "folder", "uploads",
                "resource_type", "auto"));

        FileUploadResponseDTO fileUploadResponseDTO = new FileUploadResponseDTO();
        fileUploadResponseDTO.setFileName((String) uploadResult.get("public_id"));
        fileUploadResponseDTO.setFilePath((String) uploadResult.get("secure_url"));

        UploadedFile uploadedFile = new UploadedFile();
        uploadedFile.setFileUrl(fileUploadResponseDTO.getFilePath());
        uploadedFile.setExpireAt(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));

        uploadedFile.setFileType((String) uploadResult.get("resource_type"));
        uploadedFile.setPublicId((String) uploadResult.get("public_id"));

        uploadedFileRepository.save(uploadedFile);

        return fileUploadResponseDTO;

    }

    @Transactional
    public List<FileUploadResponseDTO> uploadMultipleFilesToCloudinary(List<MultipartFile> files) throws IOException {
        List<FileUploadResponseDTO> fileUploadResponseDTOs = new ArrayList<>();

        for (MultipartFile file : files) {
            FileUploadResponseDTO fileUploadResponseDTO = uploadFileToCloudinary(file);
            fileUploadResponseDTOs.add(fileUploadResponseDTO);
        }

        return fileUploadResponseDTOs;
    }

    @Scheduled(fixedDelay = 3600000)
    public void deleteExpiredFiles() {
        List<UploadedFile> expiredFiles = uploadedFileRepository.findAllExpiredFiles(System.currentTimeMillis());

        for (UploadedFile file : expiredFiles) {
            try {
                cloudinary.uploader().destroy(file.getPublicId(), ObjectUtils.asMap(
                        "resource_type", file.getFileType()));
            } catch (Exception e) {
                logger.error("Failed to delete file from Cloudinary: " + file.getFileUrl(), e);
            }
            uploadedFileRepository.delete(file);
        }
    }

    @Transactional
    public boolean deleteFile(String fileUrl) {
        UploadedFile uploadedFile = uploadedFileRepository.findByFileUrl(fileUrl)
                .orElseThrow(() -> new NotFoundException("File Not Found"));
        uploadedFileRepository.delete(uploadedFile);
        return true;
    }

    public boolean deleteFileFromCloudinary(String fileUrl) {
        try {
            String publicId = getPublicIdFromUrl(fileUrl);
            String resourceType = getResourceTypeFromUrl(fileUrl);
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap(
                    "resource_type", resourceType));
            return true;
        } catch (Exception e) {
            logger.error("Failed to delete file from Cloudinary: " + fileUrl, e);
            return false;
        }
    }

    private String getResourceTypeFromUrl(String url) {
        if (url.contains("/image/")) {
            return "image";
        } else if (url.contains("/video/")) {
            return "video";
        } else if (url.contains("/raw/")) {
            return "raw";
        } else {
            return "auto";
        }
    }

    private String getPublicIdFromUrl(String url) {
        String[] urlParts = url.split("/");
        for (int i = 0; i < urlParts.length; i++) {
            if (urlParts[i].startsWith("v") && urlParts[i].substring(1).matches("\\d+")) {
                StringBuilder sb = new StringBuilder();
                for (int j = i + 1; j < urlParts.length; j++) {
                    sb.append(urlParts[j]).append("/");
                }
                String path = sb.substring(0, sb.length() - 1);
                return path.split("\\.")[0];
            }
        }
        return null;
    }

}
