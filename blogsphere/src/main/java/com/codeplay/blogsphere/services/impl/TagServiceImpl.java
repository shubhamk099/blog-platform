package com.codeplay.blogsphere.services.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.codeplay.blogsphere.domain.entities.Tag;
import com.codeplay.blogsphere.repository.TagRepository;
import com.codeplay.blogsphere.services.TagService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;

    @Override
    public List<Tag> getTags() {
        return tagRepository.findAllWithPostCount();
    }

    @Transactional
    @Override
    public List<Tag> createTags(Set<String> tagNames) {
        List<Tag> existingTags = tagRepository.findByNameIn(tagNames);
        Set<String> existingTagNames = existingTags
                .stream().map(tag -> tag.getName()).collect(Collectors.toSet());

        List<Tag> newTags = tagNames.stream()
                .filter(name -> !existingTagNames.contains(name))
                .map(name -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    tag.setPosts(new HashSet<>());
                    return tag;
                }).toList();

        List<Tag> savedTags = new ArrayList<>();

        if (!newTags.isEmpty()) {
            savedTags = tagRepository.saveAll(newTags);
        }

        savedTags.addAll(existingTags);
        return savedTags;
    }

    @Transactional
    @Override
    public void deleteTag(UUID id) {
        tagRepository.findById(id).ifPresent(tag -> {
            if (!tag.getPosts().isEmpty()) {
                throw new IllegalStateException("Cannnot delete tag with posts");
            }
            tagRepository.deleteById(id);
        });
    }

    @Override
    public Tag getTagById(UUID id) {
        return tagRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Tag not found with Id : " + id));
    }

    @Override
    public List<Tag> getTagByIds(Set<UUID> ids) {
        List<Tag> foundTags = tagRepository.findAllById(ids);

        if (foundTags.size() != ids.size()) {
            throw new EntityNotFoundException("Not all specified tag IDs exist");
        }

        return foundTags;
    }
}
