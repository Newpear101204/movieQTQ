package com.movie.movie.config;

import com.movie.movie.filter.JwtTokenFilter;
import com.movie.movie.service.impl.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;


import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
@EnableWebMvc
@RequiredArgsConstructor
public class SecurityConfig {


    private final JwtTokenFilter jwtTokenFilter;

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(requests -> {
                    requests
                            .requestMatchers(
                                    "/user/login", "/user/register", "/movie/"
                            )
                            .permitAll().anyRequest().authenticated();
//                            .requestMatchers(POST, "/shopqtq/createproduct").hasAnyRole("ADMIN")
//                            .requestMatchers(DELETE, "/shopqtq/deleteproduct/{id}").hasAnyRole("ADMIN")
//                            .requestMatchers(DELETE, "/shopqtq/deleteproductimage").hasAnyRole("ADMIN")
//                            .requestMatchers(POST, "/shopqtq/cart").hasAnyRole("CUS")
//                            .requestMatchers(GET, "/shopqtq/cart").hasAnyRole("CUS")
//                            .requestMatchers(GET, "/shopqtq/order").hasAnyRole("CUS", "ADMIN")
//                            .requestMatchers(POST, "/shopqtq/order").hasAnyRole("CUS", "ADMIN")
//                            .requestMatchers(GET, "/shopqtq/orderadmin").hasAnyRole("CUS", "ADMIN")
//                            .requestMatchers(GET, "/shopqtq/cart").hasAnyRole("CUS")
//                            .requestMatchers(POST, "/api/vnpay/create-payment").hasAnyRole("CUS")
//                            .requestMatchers(GET, "/api/vnpay/vnpay-return").permitAll()
//                            .requestMatchers(GET, "/shopqtq/getUsers").hasAnyRole("ADMIN")
//                            .requestMatchers(GET, "/shopqtq/getOrder/{id}").hasAnyRole("ADMIN")
//                            .requestMatchers(POST, "/shopqtq/approveOrder/{id}").hasAnyRole("ADMIN")
//                            .anyRequest().authenticated();
                });
        return http.build();
    }
}