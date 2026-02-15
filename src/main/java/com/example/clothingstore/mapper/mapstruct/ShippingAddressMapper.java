package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;

import com.example.clothingstore.dto.shippingaddress.ShippingAddressRequestDTO;
import com.example.clothingstore.dto.shippingaddress.ShippingAddressResponseDTO;
import com.example.clothingstore.model.ShippingAddress;

@Mapper(componentModel = "spring")
public interface ShippingAddressMapper {

    ShippingAddress toEntity(ShippingAddressRequestDTO shippingAddressRequestDTO);

    ShippingAddressResponseDTO toResponseDTO(ShippingAddress shippingAddress);
}