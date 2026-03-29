package com.example.clothingstore.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.clothingstore.exception.security.AccessDeniedHandlerException;
import com.example.clothingstore.exception.security.AuthenticationEntryPointException;
import com.example.clothingstore.filter.JwtAuthFilter;

import lombok.RequiredArgsConstructor;

@EnableMethodSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

        // @Autowired
        // private JwtAuthFilter jwtAuthFilter;

        // @Autowired
        // private AuthenticationEntryPointException authenticationEntryPointException;

        // @Autowired
        // private AccessDeniedHandlerException accessDeniedHandlerException;

        private final JwtAuthFilter jwtAuthFilter;

        private final AuthenticationEntryPointException authenticationEntryPointException;

        private final AccessDeniedHandlerException accessDeniedHandlerException;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

                httpSecurity.cors(cors -> cors.configurationSource((request) -> {

                        CorsConfiguration corsConfiguration = new CorsConfiguration();

                        corsConfiguration.setAllowedOrigins(List.of("*"));

                        corsConfiguration.setAllowedMethods(List.of(
                                        "GET",
                                        "POST",
                                        "PUT",
                                        "DELETE", "PATCH",
                                        "OPTIONS"));

                        corsConfiguration.setAllowedHeaders(List.of(
                                        "Authorization",
                                        "Content-Type",
                                        "Accept",
                                        "Cache-Control",
                                        "X-Requested-With",
                                        "X-Client-Version",
                                        "X-Refresh-Token"));

                        corsConfiguration.setExposedHeaders(List.of("Authorization"));

                        corsConfiguration.setAllowCredentials(null);

                        return corsConfiguration;

                }));

                httpSecurity.csrf((csrf) -> csrf.disable());

                httpSecurity.sessionManagement(
                                (session) -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                httpSecurity.authorizeHttpRequests(auth -> auth
                                // .requestMatchers("/api/**").permitAll() //
                                .requestMatchers("/v1/auth/**",
                                                "/swagger-ui/**",
                                                "/v3/api-docs/**",
                                                "/image/**",
                                                "/v1/categories/**",
                                                "/v1/products/**",
                                                "/v1/payments/zalopay/callback")
                                .permitAll() // Cho phép truy cập không cần authentication
                                .anyRequest().authenticated());

                httpSecurity.exceptionHandling(ex -> ex
                                .authenticationEntryPoint(authenticationEntryPointException) // 401
                                .accessDeniedHandler(accessDeniedHandlerException)); // 403

                httpSecurity.addFilterBefore(jwtAuthFilter,
                                UsernamePasswordAuthenticationFilter.class);

                return httpSecurity.build();
        }
}
