package com.example.clothingstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.shippingaddress.ShippingAddressRequestDTO;
import com.example.clothingstore.dto.shippingaddress.ShippingAddressResponseDTO;
import com.example.clothingstore.exception.customer.NotFoundException;
import com.example.clothingstore.mapper.ShippingAddressMapper;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.ShippingAddress;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.ShippingAddressRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShippingAddressService {

        // @Autowired
        // private ShippingAddressRepository shippingAddressRepository;

        // @Autowired
        // private CustomerRepository customerRepository;

        // @Autowired
        // private ShippingAddressMapper shippingAddressMapper;

        private final ShippingAddressRepository shippingAddressRepository;
        private final CustomerRepository customerRepository;
        private final ShippingAddressMapper shippingAddressMapper;

        @Transactional
        public List<ShippingAddressResponseDTO> getAllShippingAddresses(Integer customerId, Pageable pageable) {

                Page<ShippingAddress> shippingAddresses = shippingAddressRepository
                                .findByCustomer_CustomerId(customerId, pageable);

                return shippingAddresses
                                .map((shippingAddress) -> shippingAddressMapper
                                                .convertModelToShippingAddressResponseDTO(shippingAddress))
                                .toList();
        }

        @Transactional
        public ShippingAddressResponseDTO createShippingAddress(Integer customerId,
                        ShippingAddressRequestDTO shippingAddressRequestDTO) {

                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new NotFoundException("Invalid Customer Code"));

                ShippingAddress shippingAddress = new ShippingAddress();

                shippingAddress = shippingAddressMapper.convertShippingAddressRequestDTOToModel(
                                shippingAddressRequestDTO, shippingAddress);

                shippingAddress.setCustomer(customer);

                shippingAddressRepository.save(shippingAddress);

                return shippingAddressMapper.convertModelToShippingAddressResponseDTO(shippingAddress);
        }

        @Transactional
        public ShippingAddressResponseDTO deleteShippingAddress(Integer customerId, Integer shippingAddressId) {
                ShippingAddress shippingAddress = shippingAddressRepository
                                .findByAddressIdAndCustomer_CustomerId(shippingAddressId, customerId)
                                .orElseThrow(() -> new NotFoundException("Invalid shippingAddressId"));

                ShippingAddressResponseDTO responseDTO = shippingAddressMapper
                                .convertModelToShippingAddressResponseDTO(shippingAddress);

                shippingAddressRepository.delete(shippingAddress);

                return responseDTO;
        }

        @Transactional
        public ShippingAddressResponseDTO updateShippingAddress(Integer customerId, Integer shippingAddressId,
                        ShippingAddressRequestDTO shippingAddressRequestDTO) {
                ShippingAddress shippingAddress = shippingAddressRepository
                                .findByAddressIdAndCustomer_CustomerId(shippingAddressId, customerId)
                                .orElseThrow(() -> new NotFoundException("Invalid shippingAddressId"));

                shippingAddress = shippingAddressMapper.convertShippingAddressRequestDTOToModel(
                                shippingAddressRequestDTO, shippingAddress);

                shippingAddressRepository.save(shippingAddress);

                return shippingAddressMapper.convertModelToShippingAddressResponseDTO(shippingAddress);
        }

        @Transactional
        public List<ShippingAddressResponseDTO> getAddressesByCustomerId(Integer customerId, Pageable pageable) {
                Page<ShippingAddress> shippingAddresses = shippingAddressRepository
                                .findByCustomer_CustomerId(customerId, pageable);

                return shippingAddresses
                                .map((shippingAddress) -> shippingAddressMapper
                                                .convertModelToShippingAddressResponseDTO(shippingAddress))
                                .toList();
        }
}
