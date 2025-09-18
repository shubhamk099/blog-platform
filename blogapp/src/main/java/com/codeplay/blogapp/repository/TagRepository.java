package com.codeplay.blogapp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.codeplay.blogapp.domain.entities.Tag;

public interface TagRepository extends JpaRepository<Tag, UUID> {

}
