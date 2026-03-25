package com.example.clothingstore.model;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.AccountStatusEnum;
import com.example.clothingstore.enums.GenderEnum;
import com.example.clothingstore.enums.RoleEnum;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users_new")
@EqualsAndHashCode(callSuper = true)
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public abstract class User extends Base {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Integer userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private AccountStatusEnum status;

    @Column(name = "UserName", unique = true)
    private String userName;

    @Column(name = "Password")
    private String password;

    @Column(name = "Email")
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "Gender")
    private GenderEnum gender;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "Date")
    private LocalDateTime date;

    @Column(name = "Phone")
    private String phone;

    @Column(name = "Image")
    private String image;

    @Column(name = "Role")
    private RoleEnum role;

}
