package com.codeplay.blogsphere.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeplay.blogsphere.domain.dtos.AuthResponse;
import com.codeplay.blogsphere.domain.dtos.LoginRequest;
import com.codeplay.blogsphere.domain.dtos.SignupRequest;
import com.codeplay.blogsphere.domain.dtos.UserDto;
import com.codeplay.blogsphere.services.AuthenticationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest loginRequest) {
        UserDetails userDetails = authenticationService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword());

        String tokenValue = authenticationService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(86400)
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignupRequest signupRequest) {
        UserDetails userDetails = authenticationService.signup(signupRequest);

        String tokenValue = authenticationService.generateToken(userDetails);
        AuthResponse authResponse = AuthResponse.builder()
                .token(tokenValue)
                .expiresIn(86400)
                .build();
        return ResponseEntity.ok(authResponse);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser() {
        UserDto currentUser = authenticationService.getCurrentUser();
        return ResponseEntity.ok(currentUser);
    }
}