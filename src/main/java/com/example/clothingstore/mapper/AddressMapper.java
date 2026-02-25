package com.example.clothingstore.mapper;

import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.address.AddressRequestDTO;
import com.example.clothingstore.dto.address.AddressResponseDTO;
import com.example.clothingstore.model.Address;

@Component
public class AddressMapper {

    public Address convertShippingAddressRequestDTOToModel(
            AddressRequestDTO shippingAddressRequestDTO, Address shippingAddress) {
        shippingAddress.setRecipientName(shippingAddressRequestDTO.getRecipientName());

        shippingAddress.setPhoneNumber(shippingAddressRequestDTO.getPhoneNumber());

        shippingAddress.setDetailedAdress(shippingAddressRequestDTO.getDetailedAdress());

        shippingAddress.setWard(shippingAddressRequestDTO.getWard());

        shippingAddress.setProvince(shippingAddressRequestDTO.getProvince());
        return shippingAddress;
    }

    public AddressResponseDTO convertModelToShippingAddressResponseDTO(Address shippingAddress) {
        AddressResponseDTO shippingAddressResponseDTO = new AddressResponseDTO();

        shippingAddressResponseDTO.setAddressId(shippingAddress.getAddressId());

        shippingAddressResponseDTO.setRecipientName(shippingAddress.getRecipientName());

        shippingAddressResponseDTO.setPhoneNumber(shippingAddress.getPhoneNumber());

        shippingAddressResponseDTO.setDetailedAdress(shippingAddress.getDetailedAdress());

        shippingAddressResponseDTO.setWard(shippingAddress.getWard());

        shippingAddressResponseDTO.setProvince(shippingAddress.getProvince());

        return shippingAddressResponseDTO;
    }
}
