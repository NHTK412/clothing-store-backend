package com.example.clothingstore.filter;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.clothingstore.enums.RoleEnum;
import com.example.clothingstore.exception.business.NotFoundException;
import com.example.clothingstore.repository.UserRepository;
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

    private final JwtUtil jwtUtil;

    private final UserRepository userRepository;

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
                com.example.clothingstore.model.User user = userRepository.findByUserName(username)
                        .orElseThrow(() -> new NotFoundException("Username does not exist"));

                CustomerUserDetails userDetails = CustomerUserDetails.builder()
                        .userName(user.getUserName())
                        .password(user.getPassword())
                        .userId(user.getUserId())
                        .authorities(List.of(new SimpleGrantedAuthority(role.name())))
                        .build();

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

                usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetails(request));

                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);

            }
        }

        filterChain.doFilter(request, response);

    }

}
