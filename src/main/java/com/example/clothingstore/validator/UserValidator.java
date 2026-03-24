package com.example.clothingstore.validator;

import org.springframework.stereotype.Component;

import com.example.clothingstore.enums.RoleEnum;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.model.Address;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.User;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserValidator {

    final private UserRepository userRepository;
    final private CustomerRepository customerRepository;

    public Customer validateAndGetCustomer(String userName) {
        return customerRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public Customer validateAndGetCustomer(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer not found"));
    }

    public User validateAndGetUser(Integer userId) {
        return userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
    }

    public Address validateAndGetShippingAddress(Customer customer, Integer addressId) {
        return customer.getShippingAddresses()
                .stream()
                .filter(addr -> addr.getAddressId().equals(addressId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Address shipping not found"));
    }
}
