package com.codeplay.blogsphere.services;

import java.util.List;
import java.util.UUID;

import com.codeplay.blogsphere.domain.CreatePostRequest;
import com.codeplay.blogsphere.domain.PostStatus;
import com.codeplay.blogsphere.domain.UpdatePostRequest;
import com.codeplay.blogsphere.domain.entities.Post;
import com.codeplay.blogsphere.domain.entities.User;

public interface PostService {

    Post getPost(UUID id);

    List<Post> getAllPosts(UUID categoryId, UUID tagId, PostStatus status);

    List<Post> getDraftPosts(User user);

    Post createPost(User user, CreatePostRequest createPostRequest);

    Post updatePost(UUID id, UpdatePostRequest updatePostRequest);

    void deletePost(User user, UUID id);
}
