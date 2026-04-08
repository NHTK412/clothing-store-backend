package com.example.clothingstore.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.customer.CustomerRequestDTO;
import com.example.clothingstore.dto.customer.CustomerResponseDTO;
import com.example.clothingstore.dto.customer.CustomerSummaryDTO;
import com.example.clothingstore.enums.AccountStatusEnum;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.mapper.mapstruct.CustomerMapper;
// import com.example.clothingstore.model.Account;
import com.example.clothingstore.model.Customer;
// import com.example.clothingstore.repository.AccountRepository;
import com.example.clothingstore.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponseDTO getCustomerById(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Invalid customer ID"));
        return customerMapper.toResponseDTO(customer);
    }

    @Transactional
    public Page<CustomerSummaryDTO> getAllCustomer(Pageable pageable) {
        Page<Customer> customers = customerRepository.findAll(pageable);
        return customers.map(customerMapper::toSummaryDTO);
    }

    @Transactional
    public CustomerResponseDTO deleteCustomer(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Invalid review code"));
        customer.setStatus(AccountStatusEnum.INACTIVE);
        customerRepository.save(customer);
        return customerMapper.toResponseDTO(customer);
    }

}
