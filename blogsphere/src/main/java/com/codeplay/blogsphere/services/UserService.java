package com.codeplay.blogsphere.services;

import java.util.UUID;

import com.codeplay.blogsphere.domain.entities.User;

public interface UserService {
    User getUserById(UUID id);
}
