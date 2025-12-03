package com.codeplay.blogsphere.services;

import org.springframework.security.core.userdetails.UserDetails;

import com.codeplay.blogsphere.domain.dtos.SignupRequest;
import com.codeplay.blogsphere.domain.dtos.UserDto;

public interface AuthenticationService {

    UserDetails authenticate(String email, String password);

    String generateToken(UserDetails userDetails);

    UserDetails validateToken(String token);

    UserDetails signup(SignupRequest signupRequest);

    UserDto getCurrentUser();
}
  