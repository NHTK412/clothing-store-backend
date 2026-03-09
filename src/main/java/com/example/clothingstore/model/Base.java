package com.example.clothingstore.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass // Đánh dấu đây là một lớp cơ sở mà các lớp thực thể khác có thể kế thừa
@EntityListeners(AuditingEntityListener.class)
public abstract class Base {
    @CreatedDate
    @Column(name = "CreateAt")
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "UpdateAt")
    private LocalDateTime updateAt;

}