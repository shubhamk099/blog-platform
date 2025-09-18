package com.codeplay.blogapp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeplay.blogapp.domain.entities.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

}
