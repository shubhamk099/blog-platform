package com.codeplay.blogsphere.mappers;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.codeplay.blogsphere.domain.PostStatus;
import com.codeplay.blogsphere.domain.dtos.TagDto;
import com.codeplay.blogsphere.domain.entities.Post;
import com.codeplay.blogsphere.domain.entities.Tag;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {

    @Mapping(target = "postCount", source = "posts", qualifiedByName = "calculatePostCount")
    TagDto toTagResponse(Tag tag);

    @Named("calculatePostCount")
    default Integer calculatePostCount(Set<Post> posts) {
        if (posts == null) {
            return 0;
        }
        return (int) posts.stream().filter(post -> PostStatus.PUBLISHED.equals(post.getStatus())).count();
    }
}
