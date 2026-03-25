package com.example.clothingstore.mapper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.clothingstore.dto.customer.CustomerRequestDTO;
import com.example.clothingstore.dto.customer.CustomerResponseDTO;
import com.example.clothingstore.dto.customer.CustomerSummaryDTO;
import com.example.clothingstore.model.Customer;

@Component
public class CustomerMapper {

    public CustomerResponseDTO convertModelToCustomerResponseDTO(Customer customer) {

        CustomerResponseDTO customerResponseDTO = new CustomerResponseDTO();

        customerResponseDTO.setCustomerId(customer.getUserId());

        customerResponseDTO.setUserName(customer.getUserName());

        // customerResponseDTO.setPassword(customer.getPassword());

        customerResponseDTO.setFullName(customer.getFullName());

        customerResponseDTO.setEmail(customer.getEmail());

        customerResponseDTO.setGender(customer.getGender());

        customerResponseDTO.setDate(customer.getDate());

        customerResponseDTO.setPhone(customer.getPhone());

        // customerResponseDTO.setMembership(customer.getMembershipTier().getDescription()); ================================

        customerResponseDTO.setColorMembership(customer.getMembershipTier().getColor());

        customerResponseDTO.setStatus(customer.getStatus());

        // customerResponseDTO.setLastLogin(customer.());

        customerResponseDTO.setImage(customer.getImage());

        return customerResponseDTO;
    }

    public CustomerSummaryDTO convertModelToCustomerSummaryDTO(Customer customer) {

        CustomerSummaryDTO customerSummaryDTO = new CustomerSummaryDTO();

        customerSummaryDTO.setCustomerId(customer.getUserId());

        customerSummaryDTO.setUserName(customer.getUserName());

        // customerSummaryDTO.setPassword(customer.getPassword());

        customerSummaryDTO.setEmail(customer.getEmail());

        // customerSummaryDTO.setGender(customer.getGender());

        // customerSummaryDTO.setDate(customer.getDate());

        customerSummaryDTO.setPhone(customer.getPhone());

        // customerSummaryDTO.setMembership(customer.getMembershipTier().getDescription()); ================================

        customerSummaryDTO.setColorMembership(customer.getMembershipTier().getColor());

        customerSummaryDTO.setStatus(customer.getStatus());

        // customerSummaryDTO.setLastLogin(customer.getLastLogin());

        return customerSummaryDTO;
    }

    public Customer convertCustomerRequestDTOToModel(CustomerRequestDTO customerRequestDTO,
            Customer customer) {

        customer.setUserName(customerRequestDTO.getUserName());

        // customer.setPassword(customerRequestDTO.getPassword());

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

        // customer.setPassword(bCryptPasswordEncoder.encode(customerRequestDTO.getPassword()));

        customer.setEmail(customerRequestDTO.getEmail());

        customer.setGender(customerRequestDTO.getGender());

        customer.setFullName(customerRequestDTO.getFullName());

        customer.setDate(customerRequestDTO.getDate());

        customer.setPhone(customerRequestDTO.getPhone());

        customer.setImage(customerRequestDTO.getImage());

        return customer;
    }

}
