package com.codeplay.blogsphere.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.codeplay.blogsphere.domain.PostStatus;
import com.codeplay.blogsphere.domain.entities.Category;
import com.codeplay.blogsphere.domain.entities.Post;
import com.codeplay.blogsphere.domain.entities.Tag;
import com.codeplay.blogsphere.domain.entities.User;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {

    List<Post> findAllByStatusAndCategoryAndTagsContaining(PostStatus status, Category category, Tag tag);

    List<Post> findAllByStatusAndCategory(PostStatus status, Category category);

    List<Post> findAllByStatusAndTagsContaining(PostStatus status, Tag tag);

    List<Post> findAllByStatus(PostStatus status);

    List<Post> findAllByAuthorAndStatus(User author, PostStatus status);
}
