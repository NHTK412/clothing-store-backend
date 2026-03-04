package com.example.clothingstore.filter;

import java.io.IOException;
import java.util.List;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.clothingstore.enums.RoleEnum;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.model.Customer;
import com.example.clothingstore.model.Admin;
import com.example.clothingstore.repository.AdminRepository;
import com.example.clothingstore.repository.CustomerRepository;
import com.example.clothingstore.util.CustomerUserDetails;
import com.example.clothingstore.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    // @Autowired
    // private JwtUtil jwtUtil;

    private final JwtUtil jwtUtil;

    // @Autowired
    // private Auth accountService;

    // @Autowired
    // private CustomerRepository customerRepository;

    // @Autowired
    // private AdminRepository adminRepository;

    private final CustomerRepository customerRepository;
    private final AdminRepository adminRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");

        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer")) {
            filterChain.doFilter(request, response);
            return;
        }

        final String token = authorizationHeader.substring(7);

        final String username = jwtUtil.getUserName(token);

        final RoleEnum role = jwtUtil.getRole(token);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.isTokenVaid(token)) {
                // UserDetails customerUserDetails =
                // accountService.getAccountByUsername(username);

                // UserDetails userDetails = null;
                CustomerUserDetails userDetails = null;

                if (role == RoleEnum.ROLE_CUSTOMER) {
                    Customer customer = customerRepository.findByUserName(username)
                            .orElseThrow(() -> new NotFoundException("Username does not exist"));

                    // userDetails = User.builder()
                    // .username(customer.getUserName())
                    // .password(customer.getPassword())
                    // .authorities(List.of(new
                    // SimpleGrantedAuthority(RoleEnum.ROLE_CUSTOMER.name())))
                    // .build();
                    userDetails = CustomerUserDetails.builder()
                            .userName(customer.getUserName())
                            .password(customer.getPassword())
                            .userId(customer.getCustomerId())
                            .authorities(List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_CUSTOMER.name())))
                            .build();
                } else if (role == RoleEnum.ROLE_ADMIN) {
                    Admin admin = adminRepository.findByUserName(username)
                            .orElseThrow(() -> new NotFoundException("Username does not exist"));

                    // userDetails = User.builder()
                    // .username(admin.getUserName())
                    // .password(admin.getPassword())
                    // .authorities(List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_ADMIN.name())))
                    // .build();

                    userDetails = CustomerUserDetails.builder()
                            .userName(admin.getUserName())
                            .password(admin.getPassword())
                            .userId(admin.getAdminId())
                            .authorities(List.of(new SimpleGrantedAuthority(RoleEnum.ROLE_ADMIN.name())))
                            .build();

                } else {
                    throw new RuntimeException("Role does not exist");
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }

        filterChain.doFilter(request, response);

    }

}
