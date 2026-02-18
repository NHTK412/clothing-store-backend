package com.example.clothingstore.model;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "MembershipTier")
@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class MembershipTier extends Base {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TiedId")
    private Integer tiedId;

    @Column(name = "MinimumSpending")
    private Double minimumSpending; // Số tiền tối thiểu để đạt được hạng thành viên này

    @Column(name = "Color")
    private String color;

    @Column(name = "TierName")
    private String tierName; // Tên hạng thành viên (ví dụ: Đồng, Bạc, Vàng, Bạch Kim)

    @OneToMany(mappedBy = "membershipTier")
    private List<Customer> customers;

}
