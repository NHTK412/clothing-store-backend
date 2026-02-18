package com.example.clothingstore.mapper.mapstruct;

import org.aspectj.lang.annotation.After;
import org.mapstruct.AfterMapping;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.clothingstore.dto.customer.CustomerRequestDTO;
import com.example.clothingstore.dto.customer.CustomerResponseDTO;
import com.example.clothingstore.dto.customer.CustomerSummaryDTO;
import com.example.clothingstore.model.Customer;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    // @Mapping(source = "membershipTier.description", target = "membership")
    @Mapping(source = "membershipTier.color", target = "colorMembership")
    CustomerResponseDTO toResponseDTO(Customer customer);

    // @Mapping(source = "membershipTier.description", target = "membership")
    @Mapping(source = "membershipTier.color", target = "colorMembership")
    CustomerSummaryDTO toSummaryDTO(Customer customer);

    // pstruct/CustomerMapper.java:[25,14] Unmapped target properties: "status,
    // password, membership, reviews, lastLogin, orders, membershipTier,
    // shippingAddresses".

    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "membership", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "membershipTier", ignore = true)
    @Mapping(target = "shippingAddresses", ignore = true)
    Customer toModel(CustomerRequestDTO customerRequestDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "membership", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "lastLogin", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "membershipTier", ignore = true)
    @Mapping(target = "shippingAddresses", ignore = true)
    void updateModelFromDTO(CustomerRequestDTO customerRequestDTO, @MappingTarget Customer customer);

}