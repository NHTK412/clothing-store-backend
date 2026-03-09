package com.example.clothingstore.model;

import java.time.LocalDateTime;

import com.example.clothingstore.enums.AccountStatusEnum;
import com.example.clothingstore.enums.GenderEnum;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity
// @Table(name = "Admin")
// @NoArgsConstructor
// @AllArgsConstructor
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class Admin extends Base {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "AdminId")
//     private Integer adminId;

//     @Column(name = "Email")
//     private String email;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "Gender")
//     private GenderEnum gender;

//     @Column(name = "FullName")
//     private String fullName;

//     @Column(name = "Date")
//     private LocalDateTime date;

//     @Column(name = "Phone")
//     private String phone;

//     @Column(name = "Address")
//     private String address;

//     // @OneToOne
//     // @JoinColumn(name = "AccountId")
//     // private Account account;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "Status")
//     private AccountStatusEnum status;

//     @Column(name = "UserName")
//     private String userName;

//     @Column(name = "Password")
//     private String password;

//     @Column(name = "LastLogin")
//     private LocalDateTime lastLogin;

//     @Column(name = "Image")
//     private String image;

// }

@Entity
@Table(name = "Admin_new")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
public class Admin extends User {

    @OneToMany(mappedBy = "admin")
    private java.util.List<Promotion> promotions;

}