package com.example.clothingstore.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.clothingstore.model.UploadedFile;

@Repository
public interface UploadedFileRepository extends JpaRepository<UploadedFile, Long> {

    @Query("Select u from UploadedFile u where u.expireAt < :currentTime")
    java.util.List<UploadedFile> findAllExpiredFiles(Long currentTime);

    @Query("Select u from UploadedFile u where u.fileUrl = :fileUrl")
    Optional<UploadedFile> findByFileUrl(String fileUrl);
}
