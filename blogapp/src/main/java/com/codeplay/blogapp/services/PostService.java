package com.codeplay.blogapp.services;

import java.util.List;
import java.util.UUID;

import com.codeplay.blogapp.domain.CreatePostRequest;
import com.codeplay.blogapp.domain.PostStatus;
import com.codeplay.blogapp.domain.UpdatePostRequest;
import com.codeplay.blogapp.domain.entities.Post;
import com.codeplay.blogapp.domain.entities.User;

public interface PostService {

    Post getPost(UUID id);

    List<Post> getAllPosts(UUID categoryId, UUID tagId, PostStatus status);

    List<Post> getDraftPosts(User user);

    Post createPost(User user, CreatePostRequest createPostRequest);

    Post updatePost(UUID id, UpdatePostRequest updatePostRequest);

    void deletePost(UUID id);
}
