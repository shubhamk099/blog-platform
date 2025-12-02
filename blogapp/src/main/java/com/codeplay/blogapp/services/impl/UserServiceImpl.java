package com.codeplay.blogapp.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.codeplay.blogapp.domain.entities.User;
import com.codeplay.blogapp.repository.UserRepository;
import com.codeplay.blogapp.services.UserService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with Id : " + id));
    }
}
