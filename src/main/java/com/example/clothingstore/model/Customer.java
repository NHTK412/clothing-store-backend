package com.example.clothingstore.model;

import java.time.LocalDateTime;
import java.util.List;

import com.example.clothingstore.enums.AccountStatusEnum;
import com.example.clothingstore.enums.GenderEnum;

import jakarta.persistence.EnumType;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// @Entity
// @Table(name = "Customer")
// @NoArgsConstructor
// @AllArgsConstructor
// @Data
// @EqualsAndHashCode(callSuper = true)
// public class Customer extends Base {

//     @Id
//     @GeneratedValue(strategy = GenerationType.IDENTITY)
//     @Column(name = "CustomerId")
//     private Integer customerId;

//     @Enumerated(EnumType.STRING)
//     @Column(name = "Status")
//     private AccountStatusEnum status;

//     @Column(name = "UserName")
//     private String userName;

//     @Column(name = "Password")
//     private String password;

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

//     @Column(name = "Membership")
//     private String membership;

//     @OneToMany(mappedBy = "customer")
//     private List<Review> reviews;

//     @Column(name = "LastLogin")
//     private LocalDateTime lastLogin;

//     // @OneToOne(cascade = CascadeType.ALL)
//     // @JoinColumn(name = "AccountId")
//     // private Account account;

//     @OneToMany(mappedBy = "customer")
//     private List<Order> orders;

//     @ManyToOne(fetch = FetchType.LAZY)
//     @JoinColumn(name = "TiedId")
//     private MembershipTier membershipTier;

//     @OneToMany(mappedBy = "customer")
//     private List<Address> shippingAddresses;

//     @Column(name = "Image")
//     private String image;
// }

@Entity
@Getter
@Setter
@Table(name = "Customer_new")
@EqualsAndHashCode(callSuper = true)
public class Customer extends User {

    @OneToMany(mappedBy = "customer")
    private List<Review> reviews;

    @OneToMany(mappedBy = "customer")
    private List<Order> orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TiedId")
    private MembershipTier membershipTier;

    @OneToMany(mappedBy = "customer")
    private List<Address> shippingAddresses;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VoucherWallet> voucherWallets;

}