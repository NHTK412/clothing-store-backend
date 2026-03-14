package com.example.clothingstore.service;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.codec.Hex;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException.Conflict;

import com.example.clothingstore.dto.auth.AuthResponseDTO;
import com.example.clothingstore.enums.AccountStatusEnum;
import com.example.clothingstore.enums.RoleEnum;
import com.example.clothingstore.exception.business.ConflictException;
import com.example.clothingstore.exception.business.InvalidRefreshTokenException;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.model.Admin;
import com.example.clothingstore.model.Cart;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.User;
import com.example.clothingstore.repository.AdminRepository;
import com.example.clothingstore.repository.CartRepository;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.repository.MembershipTierRepository;
import com.example.clothingstore.repository.UserRepository;
import com.example.clothingstore.util.JwtUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

// import ch.qos.logback.core.testUtil.RandomUtil;

@Service
@RequiredArgsConstructor
public class AuthService {

    // @Autowired
    // AccountRepository accountRepository;

    // @Autowired
    // private CustomerRepository customerRepository;

    // @Autowired
    // private AdminRepository adminRepository;

    // @Autowired
    // private MembershipTierRepository membershipTierRepository;

    // @Autowired
    // private CartRepository cartRepository;

    // @Autowired
    // JwtUtil jwtUtil;

    @Value("${api-key}")
    private String apiKey;

    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final MembershipTierRepository membershipTierRepository;
    private final CartRepository cartRepository;
    private final JwtUtil jwtUtil;

    private final RedisTemplate<String, Object> redisTemplate;

    private static long expiration = 1000 * 60 * 60 * 4; // 4h

    final private SecureRandom secureRandom = new SecureRandom();

    // @Autowired
    // private RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public AuthResponseDTO register(String userName, String password) {

        if (userRepository.existsByUserName(userName)) {
            throw new ConflictException("Username already exists");
        }

        Customer customer = new Customer();

        customer.setUserName(userName);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String encodedPassword = encoder.encode(password);

        customer.setPassword(encodedPassword);

        customer.setStatus(AccountStatusEnum.ACTIVE);

        customer.setMembershipTier(membershipTierRepository.findByTierName("BRONZE").get());

        customerRepository.save(customer);

        customer.setRole(RoleEnum.ROLE_CUSTOMER);

        Cart cart = new Cart();
        cart.setCustomer(customer);
        cartRepository.save(cart);

        // return login(userName, password, false);
        return login_v2(userName, password);
    }

    @Transactional
    public AuthResponseDTO registerAdmin(String userName, String password, String apiKey) {

        if (userRepository.existsByUserName(userName)) {
            throw new ConflictException("Username already exists");
        }

        Admin admin = new Admin();

        if (!this.apiKey.equals(apiKey)) {
            throw new InvalidRefreshTokenException("API key is invalid");
        }

        admin.setUserName(userName);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode(password);
        admin.setPassword(encodedPassword);
        admin.setStatus(AccountStatusEnum.ACTIVE);
        adminRepository.save(admin);
        admin.setRole(RoleEnum.ROLE_ADMIN);

        return login_v2(userName, password);

    }

    // public AuthResponseDTO login(String userName, String password) {
    // Customer customer = customerRepository.findByUserName(userName)
    // .orElseThrow(() -> new RuntimeException("Username does not exist"));

    // BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    // String code = encoder.encode(password);
    // System.out.println(code);

    // // if (!employee.getPassword().equals(password)) {
    // if (!encoder.matches(password, customer.getPassword())) {
    // throw new InvalidRefreshTokenException("Password is invalid");
    // }

    // String accessToken = jwtUtil.generateToken(customer.getUserName(),
    // RoleEnum.ROLE_CUSTOMER.name(),
    // expiration);

    // byte[] bytes = new byte[50];

    // secureRandom.nextBytes(bytes);

    // // String refreshToken = Hex.encodeHexString(bytes);

    // String refreshToken = new String(Hex.encode(bytes));

    // // redisTemplate.opsForValue().set("refreshToken::" + refreshToken,
    // // employee.getUsername(), 7, TimeUnit.DAYS);

    // AuthResponseDTO authResponseDTO = new AuthResponseDTO();
    // authResponseDTO.setUsername(customer.getUserName());
    // authResponseDTO.setRole(RoleEnum.ROLE_CUSTOMER.name());
    // authResponseDTO.setAccessToken(accessToken);
    // authResponseDTO.setRefreshToken(refreshToken);
    // authResponseDTO.setExpiresIn(expiration);
    // return authResponseDTO;
    // }

    @Transactional
    public AuthResponseDTO login(String userName, String password) {

        // if (admin) {
        // return loginAdmin(userName, password);
        // }
        Customer customer = customerRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Username does not exist"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String code = encoder.encode(password);
        System.out.println(code);

        // if (!employee.getPassword().equals(password)) {
        if (!encoder.matches(password, customer.getPassword())) {
            throw new InvalidRefreshTokenException("Password is invalid");
        }

        // String accessToken = jwtUtil.generateToken(customer.getUserName(),
        // RoleEnum.ROLE_CUSTOMER.name(),
        // expiration);

        String accessToken = jwtUtil.generateToken(
                customer.getUserName(),
                customer.getRole().name(),
                expiration);

        byte[] bytes = new byte[50];

        secureRandom.nextBytes(bytes);

        // String refreshToken = Hex.encodeHexString(bytes);

        String refreshToken = new String(Hex.encode(bytes));

        redisTemplate.opsForValue().set(
                "refreshToken::" + refreshToken,
                customer.getUserId(),
                7,
                java.util.concurrent.TimeUnit.DAYS);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setUsername(customer.getUserName());
        authResponseDTO.setRole(RoleEnum.ROLE_CUSTOMER.name());
        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);
        authResponseDTO.setExpiresIn(expiration);
        return authResponseDTO;
    }

    @Transactional
    public AuthResponseDTO loginAdmin(String userName, String password) {
        Admin admin = adminRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Username does not exist"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String code = encoder.encode(password);
        System.out.println(code);

        // if (!employee.getPassword().equals(password)) {
        if (!encoder.matches(password, admin.getPassword())) {
            throw new InvalidRefreshTokenException("Password is invalid");
        }

        String accessToken = jwtUtil.generateToken(admin.getUserName(), RoleEnum.ROLE_ADMIN.name(),
                expiration);

        byte[] bytes = new byte[50];

        secureRandom.nextBytes(bytes);

        // String refreshToken = Hex.encodeHexString(bytes);

        String refreshToken = new String(Hex.encode(bytes));

        redisTemplate.opsForValue().set(
                "refreshToken-admin::" + refreshToken,
                admin.getUserId(),
                7,
                java.util.concurrent.TimeUnit.DAYS);

        // redisTemplate.opsForValue().set("refreshToken::" + refreshToken,
        // employee.getUsername(), 7, TimeUnit.DAYS);

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setUsername(admin.getUserName());
        authResponseDTO.setRole(RoleEnum.ROLE_ADMIN.name());
        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);
        authResponseDTO.setExpiresIn(expiration);
        return authResponseDTO;
    }

    // public AuthResponseDTO getAccessTokenWithRefreshToken(String refreshToken) {

    // // String userName = (String)
    // redisTemplate.opsForValue().get("refreshToken::" +
    // // refreshToken);

    // if (userName == null) {
    // throw new InvalidRefreshTokenException("Refresh token is invalid");
    // }

    // Employee employee = employeeRepository.findByUsername(userName)
    // .orElseThrow(() -> new RuntimeException("Username does not exist"));

    // String accessToken = jwtUtil.generateToken(employee.getUsername(),
    // employee.getRole().name(),
    // expiration);

    // AuthResponseDTO authResponseDTO = new AuthResponseDTO();
    // authResponseDTO.setUsername(employee.getUsername());
    // authResponseDTO.setRole(employee.getRole().name());
    // authResponseDTO.setAccessToken(accessToken);
    // authResponseDTO.setRefreshToken(refreshToken);
    // authResponseDTO.setExpiresIn(expiration);
    // return authResponseDTO;
    // }

    @Transactional
    public AuthResponseDTO login_v2(String userName, String password) {

        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new NotFoundException("Username does not exist"));

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // String code = encoder.encode(password);
        // System.out.println(code);

        // if (!employee.getPassword().equals(password)) {
        if (!encoder.matches(password, user.getPassword())) {
            throw new InvalidRefreshTokenException("Password is invalid");
        }

        String accessToken = jwtUtil.generateToken(
                user.getUserName(),
                user.getRole().name(),
                expiration);

        byte[] bytes = new byte[50];

        secureRandom.nextBytes(bytes);

        // String refreshToken = new String(Hex.encode(bytes));
        String refreshToken = jwtUtil.generateToken(
                user.getUserName(),
                user.getRole().name(),
                // expiration * 7); // Refresh token có thời gian sống lâu hơn access token
                Long.valueOf(1000 * 60 * 60 * 24 * 7)); // Refresh token có thời gian sống lâu hơn access token, ở đây
                                                        // là 7 ngày

        // ====================================================================================
        // Tạm thời để vậy vì redis docker đang lỗi k connect được nên nếu không kết nối
        // được thì bỏ qua không lưu vào redis
        try {
            redisTemplate.opsForValue().set(
                    // "refreshToken::" + refreshToken,
                    "refreshToken::" + user.getUserId(),
                    refreshToken,
                    // user.getUserId(),
                    7,
                    java.util.concurrent.TimeUnit.DAYS);
        } catch (Exception e) {
            System.out.println("Error storing refresh token in Redis: " + e.getMessage());
        }
        // =====================================================================================

        AuthResponseDTO authResponseDTO = new AuthResponseDTO();
        authResponseDTO.setUsername(user.getUserName());
        authResponseDTO.setRole(user.getRole().name());
        authResponseDTO.setAccessToken(accessToken);
        authResponseDTO.setRefreshToken(refreshToken);
        authResponseDTO.setExpiresIn(expiration);
        return authResponseDTO;
    }

    public AuthResponseDTO getAccessTokenWithRefreshToken(String refreshToken, boolean isAdmin) {

        String redisKey = isAdmin ? "refreshToken-admin::" : "refreshToken::";
        // String redisKey = "refreshToken::";

        Integer userId = (Integer) redisTemplate.opsForValue().get(redisKey + refreshToken);

        if (userId == null) {
            throw new InvalidRefreshTokenException("Refresh token is invalid");
        }

        if (isAdmin) {
            Admin admin = adminRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User does not exist"));

            String accessToken = jwtUtil.generateToken(admin.getUserName(), RoleEnum.ROLE_ADMIN.name(),
                    expiration);

            AuthResponseDTO authResponseDTO = new AuthResponseDTO();
            authResponseDTO.setUsername(admin.getUserName());
            authResponseDTO.setRole(RoleEnum.ROLE_ADMIN.name());
            authResponseDTO.setAccessToken(accessToken);
            authResponseDTO.setRefreshToken(refreshToken);
            authResponseDTO.setExpiresIn(expiration);

            return authResponseDTO;
        } else {
            Customer customer = customerRepository.findById(userId)
                    .orElseThrow(() -> new NotFoundException("User does not exist"));

            String accessToken = jwtUtil.generateToken(customer.getUserName(), RoleEnum.ROLE_CUSTOMER.name(),
                    expiration);

            AuthResponseDTO authResponseDTO = new AuthResponseDTO();
            authResponseDTO.setUsername(customer.getUserName());
            authResponseDTO.setRole(RoleEnum.ROLE_CUSTOMER.name());
            authResponseDTO.setAccessToken(accessToken);
            authResponseDTO.setRefreshToken(refreshToken);
            authResponseDTO.setExpiresIn(expiration);

            return authResponseDTO;

        }
    }

}
