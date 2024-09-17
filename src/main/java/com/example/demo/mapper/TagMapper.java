package com.example.demo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.Tag;
import com.example.demo.request.TagCreateRequest;
import com.example.demo.response.TagResponse;
import com.example.demo.service.UserService;

@Component
public class TagMapper implements EntityMapper<Tag, TagResponse, TagCreateRequest>{
    @Autowired
    private UserService userService;
    
    @Override
    public TagResponse toBasicResponse(Tag tag) {
        TagResponse tagResponse = new TagResponse();

        tagResponse.setName(tag.getName());
        tagResponse.setId(tag.getId());

        return tagResponse;
    }

    @Override
    public List<TagResponse> toBasicResponse(List<Tag> tags) {
        if(tags == null || tags.isEmpty()) return Collections.emptyList();
        return tags.stream().map((tag) -> {return this.toBasicResponse(tag);}).collect(Collectors.toList());
    }

    @Override
    public Tag toModel(TagCreateRequest dto) {
        Tag tag = new Tag();
        tag.setAccount(userService.getCurrentUser().getAccount());
        tag.setName(dto.getName());
        tag.setId(dto.getId());
        return tag;
    }

}
