package com.example.clothingstore.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.clothingstore.dto.customer.CustomerRequestDTO;
import com.example.clothingstore.dto.customer.CustomerResponseDTO;
import com.example.clothingstore.dto.customer.CustomerSummaryDTO;
import com.example.clothingstore.enums.AccountStatusEnum;
import com.example.clothingstore.exception.customer.NotFoundException;
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

    // @Autowired
    // private CustomerRepository customerRepository;

    // @Autowired
    // private AccountRepository accountRepository;

    // @Autowired
    // private CustomerMapper customerMapper;

    private final CustomerRepository customerRepository;
    // private final AccountRepository accountRepository;
    private final CustomerMapper customerMapper;

    @Transactional
    public CustomerResponseDTO getCustomerById(Integer customerId) {

        // Customer customer =
        // customerRepository.findByIdWithAccountAndMembershipTier(customerId)
        // .orElseThrow(() -> new NotFoundException("Mã khách hàng không hợp lệ"));

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Mã khách hàng không hợp lệ"));

        
        return customerMapper.toResponseDTO(customer);

    }

    
    @Transactional
    public List<CustomerSummaryDTO> getAllCustomer(Pageable pageable) {

        // Page<Customer> customers =
        // customerRepository.findAllWithAccountAndMembershipTier(pageable);

        Page<Customer> customers = customerRepository.findAll(pageable);

        return customers.stream()
                .map((customer) -> customerMapper.toSummaryDTO(customer))
                .toList();

        // List<CustomerSummaryDTO> customerSummaryDTOs =
        // customer.stream().map(customer)

        // return CustomerMapper.convertModelToCustomerResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO deleteCustomer(Integer customerId) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Invalid review code"));

        customer.setStatus(AccountStatusEnum.INACTIVE);

        customerRepository.save(customer);

        return customerMapper.toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO createCustomer(CustomerRequestDTO customerRequestDTO) {

        // Account account = accountRepository.findById(accountId)
        // .orElseThrow(() -> new NotFoundException("Tài khoản không tồn tại"));
        Customer customer = new Customer();

        customerMapper.updateModelFromDTO(customerRequestDTO, customer);

        customerRepository.save(customer);

        return customerMapper.toResponseDTO(customer);
    }

    @Transactional
    public CustomerResponseDTO updateCustomer(Integer customerId, CustomerRequestDTO customerRequestDTO) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Invalid customer code"));

        customerMapper.updateModelFromDTO(customerRequestDTO, customer);
        customerRepository.save(customer);

        return customerMapper.toResponseDTO(customer);
    }
}
