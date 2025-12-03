package com.codeplay.blogsphere.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.codeplay.blogsphere.domain.CreatePostRequest;
import com.codeplay.blogsphere.domain.UpdatePostRequest;
import com.codeplay.blogsphere.domain.dtos.CreatePostRequestDto;
import com.codeplay.blogsphere.domain.dtos.PostDto;
import com.codeplay.blogsphere.domain.dtos.UpdatePostRequestDto;
import com.codeplay.blogsphere.domain.entities.Post;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PostMapper {

    @Mapping(target = "author", source = "author")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "tags", source = "tags")
    PostDto toDto(Post post);

    CreatePostRequest toCreatePostRequest(CreatePostRequestDto dto);

    UpdatePostRequest toUpdatePostRequest(UpdatePostRequestDto dto);
}
