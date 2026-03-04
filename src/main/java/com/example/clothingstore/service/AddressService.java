package com.example.clothingstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.address.AddressRequestDTO;
import com.example.clothingstore.dto.address.AddressResponseDTO;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.AddressMapper;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Address;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.AddressRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AddressService {

        // @Autowired
        // private ShippingAddressRepository shippingAddressRepository;

        // @Autowired
        // private CustomerRepository customerRepository;

        // @Autowired
        // private ShippingAddressMapper shippingAddressMapper;

        private final AddressRepository shippingAddressRepository;
        private final CustomerRepository customerRepository;
        private final AddressMapper shippingAddressMapper;

        private final com.example.clothingstore.mapper.mapstruct.AddressMapper addressMapper;

        // @Transactional
        // public List<AddressResponseDTO> getAllShippingAddresses(Integer customerId,
        // Pageable pageable) {

        // Page<Address> shippingAddresses = shippingAddressRepository
        // .findByCustomer_CustomerId(customerId, pageable);

        // return shippingAddresses
        // .map((shippingAddress) -> shippingAddressMapper
        // .convertModelToShippingAddressResponseDTO(shippingAddress))
        // .toList();
        // }

        @Transactional
        public AddressResponseDTO createShippingAddress(Integer customerId,
                        AddressRequestDTO shippingAddressRequestDTO) {

                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new NotFoundException("Invalid Customer Code"));

                Address shippingAddress = new Address();

                shippingAddress = shippingAddressMapper.convertShippingAddressRequestDTOToModel(
                                shippingAddressRequestDTO, shippingAddress);

                shippingAddress.setCustomer(customer);

                shippingAddressRepository.save(shippingAddress);

                return shippingAddressMapper.convertModelToShippingAddressResponseDTO(shippingAddress);
        }

        @Transactional
        public AddressResponseDTO deleteShippingAddress(Integer customerId, Integer shippingAddressId) {
                Address shippingAddress = shippingAddressRepository
                                .findByAddressIdAndCustomer_CustomerId(shippingAddressId, customerId)
                                .orElseThrow(() -> new NotFoundException("Invalid shippingAddressId"));

                AddressResponseDTO responseDTO = shippingAddressMapper
                                .convertModelToShippingAddressResponseDTO(shippingAddress);

                shippingAddressRepository.delete(shippingAddress);

                return responseDTO;
        }

        @Transactional
        public AddressResponseDTO updateShippingAddress(Integer customerId, Integer shippingAddressId,
                        AddressRequestDTO shippingAddressRequestDTO) {
                Address shippingAddress = shippingAddressRepository
                                .findByAddressIdAndCustomer_CustomerId(shippingAddressId, customerId)
                                .orElseThrow(() -> new NotFoundException("Invalid shippingAddressId"));

                shippingAddress = shippingAddressMapper.convertShippingAddressRequestDTOToModel(
                                shippingAddressRequestDTO, shippingAddress);

                shippingAddressRepository.save(shippingAddress);

                return shippingAddressMapper.convertModelToShippingAddressResponseDTO(shippingAddress);
        }

        @Transactional
        public Page<AddressResponseDTO> getAddressesByCustomerId(Integer customerId, Pageable pageable) {
                Page<Address> shippingAddresses = shippingAddressRepository
                                .findByCustomer_CustomerId(customerId, pageable);

                return shippingAddresses.map(addressMapper::toResponseDTO);

                // return shippingAddresses
                //                 .map((shippingAddress) -> shippingAddressMapper
                //                                 .convertModelToShippingAddressResponseDTO(shippingAddress))
                //                 .toList();


        }
}
