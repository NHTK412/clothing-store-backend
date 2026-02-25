package com.example.clothingstore.mapper.mapstruct;

import org.mapstruct.Mapper;

import com.example.clothingstore.dto.address.AddressRequestDTO;
import com.example.clothingstore.dto.address.AddressResponseDTO;
import com.example.clothingstore.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toEntity(AddressRequestDTO shippingAddressRequestDTO);

    AddressResponseDTO toResponseDTO(Address shippingAddress);
}