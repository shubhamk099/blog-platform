package com.codeplay.blogsphere.services.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.codeplay.blogsphere.domain.CreatePostRequest;
import com.codeplay.blogsphere.domain.PostStatus;
import com.codeplay.blogsphere.domain.UpdatePostRequest;
import com.codeplay.blogsphere.domain.entities.Category;
import com.codeplay.blogsphere.domain.entities.Post;
import com.codeplay.blogsphere.domain.entities.Tag;
import com.codeplay.blogsphere.domain.entities.User;
import com.codeplay.blogsphere.repository.PostRepository;
import com.codeplay.blogsphere.services.CategoryService;
import com.codeplay.blogsphere.services.PostService;
import com.codeplay.blogsphere.services.TagService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final CategoryService categoryService;
    private final TagService tagService;

    private static final int WORDS_PER_MINUTE = 200;

    @Override
    @Transactional(readOnly = true)
    public List<Post> getAllPosts(UUID categoryId, UUID tagId, PostStatus status) {

        PostStatus filterStatus = (status != null) ? status : PostStatus.PUBLISHED;

        if (null != categoryId && null != tagId) {
            Category category = categoryService.getCategoryById(categoryId);

            Tag tag = tagService.getTagById(tagId);

            return postRepository.findAllByStatusAndCategoryAndTagsContaining(filterStatus, category, tag);
        }

        if (null != categoryId) {
            Category category = categoryService.getCategoryById(categoryId);

            return postRepository.findAllByStatusAndCategory(filterStatus, category);
        }

        if (null != tagId) {
            Tag tag = tagService.getTagById(tagId);
            return postRepository.findAllByStatusAndTagsContaining(filterStatus, tag);
        }

        return postRepository.findAllByStatus(filterStatus);
    }

    @Override
    public List<Post> getDraftPosts(User user) {
        return postRepository.findAllByAuthorAndStatus(user, PostStatus.DRAFT);
    }

    @Override
    @Transactional
    public Post createPost(User user, CreatePostRequest createPostRequest) {
        Post newPost = new Post();
        newPost.setTitle(createPostRequest.getTitle());
        newPost.setContent(createPostRequest.getContent());
        newPost.setStatus(createPostRequest.getStatus());
        newPost.setAuthor(user);
        newPost.setReadingTime(calculateReadingTime(createPostRequest.getContent()));

        Category category = categoryService.getCategoryById(createPostRequest.getCategoryId());
        newPost.setCategory(category);

        Set<UUID> tagIds = createPostRequest.getTagIds();

        List<Tag> tags = tagService.getTagByIds(tagIds);
        newPost.setTags(new HashSet<>(tags));

        return postRepository.save(newPost);
    }

    private Integer calculateReadingTime(String content) {
        if (content == null || content.isEmpty())
            return 0;

        int wordCount = content.trim().split("\\s+").length;
        return (int) Math.ceil((double) wordCount / WORDS_PER_MINUTE);
    }

    @Override
    @Transactional
    public Post updatePost(UUID id, UpdatePostRequest updatePostRequest) {
        Post existingPost = postRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Post does not exist with Id : " + id));

        existingPost.setTitle(updatePostRequest.getTitle());
        String postContent = updatePostRequest.getContent();
        existingPost.setContent(postContent);
        existingPost.setStatus(updatePostRequest.getStatus());
        existingPost.setReadingTime(calculateReadingTime(postContent));

        UUID updatePostRequestCategoryId = updatePostRequest.getCategoryId();

        if (!existingPost.getCategory().getId().equals(updatePostRequestCategoryId)) {
            Category newCategory = categoryService.getCategoryById(updatePostRequestCategoryId);
            existingPost.setCategory(newCategory);
        }

        Set<UUID> existingTagIds = existingPost.getTags().stream().map(Tag::getId).collect(Collectors.toSet());
        Set<UUID> updatePostRequestTagIds = updatePostRequest.getTagIds();

        if (!existingTagIds.equals(updatePostRequestTagIds)) {
            List<Tag> newTags = tagService.getTagByIds(updatePostRequestTagIds);
            existingPost.setTags(new HashSet<>(newTags));
        }

        return postRepository.save(existingPost);
    }

    @Override
    public Post getPost(UUID id) {
        return postRepository.findById(id)
                .orElseThrow((() -> new EntityNotFoundException("Post does not exist with Id : " + id)));
    }

    @Override
    public void deletePost(User user, UUID id) {
        Post post = getPost(id);

        if (!post.getAuthor().getId().equals(user.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this post.");
        }

        postRepository.delete(post);
    }
}
