package com.codeplay.blogsphere.services.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.codeplay.blogsphere.domain.dtos.SignupRequest;
import com.codeplay.blogsphere.domain.dtos.UserDto;
import com.codeplay.blogsphere.domain.entities.User;
import com.codeplay.blogsphere.repository.UserRepository;
import com.codeplay.blogsphere.security.BlogUserDetails;
import com.codeplay.blogsphere.services.AuthenticationService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secretKey;

    private static final Long JWT_EXPIRATION_IN_MS = 86400000L; // 24 hours

    @Override
    public UserDetails authenticate(String email, String password) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password));

        return userDetailsService.loadUserByUsername(email);
    }

    @Override
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        String token = Jwts.builder()
                .claims(claims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_IN_MS))
                .signWith(getSigningKey())
                .compact();

        return token;
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = secretKey.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public UserDetails validateToken(String token) {
        String username = extractUsername(token);
        return userDetailsService.loadUserByUsername(username);
    }

    @Override
    public UserDetails signup(SignupRequest signupRequest) {
        // Check if user already exists
        if (userRepository.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        User user = User.builder()
                .name(signupRequest.getName())
                .email(signupRequest.getEmail())
                .password(passwordEncoder.encode(signupRequest.getPassword()))
                .build();

        userRepository.save(user);

        return userDetailsService.loadUserByUsername(signupRequest.getEmail());
    }

    @Override
    public UserDto getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof BlogUserDetails) {
            BlogUserDetails blogUserDetails = (BlogUserDetails) principal;
            return UserDto.builder()
                    .id(blogUserDetails.getUser().getId().toString())
                    .name(blogUserDetails.getUser().getName())
                    .email(blogUserDetails.getUser().getEmail())
                    .build();
        }
        throw new IllegalStateException("User not authenticated or principal is not BlogUserDetails");
    }

    private String extractUsername(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
}