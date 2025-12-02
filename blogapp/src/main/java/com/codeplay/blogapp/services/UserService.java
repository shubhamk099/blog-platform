package com.codeplay.blogapp.services;

import java.util.UUID;

import com.codeplay.blogapp.domain.entities.User;

public interface UserService {
    User getUserById(UUID id);
}
