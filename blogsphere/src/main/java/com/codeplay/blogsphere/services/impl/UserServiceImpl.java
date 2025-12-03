package com.codeplay.blogsphere.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.codeplay.blogsphere.domain.entities.User;
import com.codeplay.blogsphere.repository.UserRepository;
import com.codeplay.blogsphere.services.UserService;

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
