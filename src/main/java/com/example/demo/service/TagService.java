package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.model.Tag;
import com.example.demo.repository.TagRespository;
import com.example.demo.response.TagResponse;

@Service
public class TagService {

    @Autowired
    private TagRespository tagRespository;

    public TagResponse toTagResponse(Tag tag) {
        TagResponse tagResponse = new TagResponse();

        tagResponse.setName(tag.getName());
        tagResponse.setId(tag.getId());

        return tagResponse;
    }

    public Page<TagResponse> getAllTagsByAccount(Long accountId, Pageable pageable) {
        Page<Tag> tags = tagRespository.findByAccountId(accountId, pageable);
        return tags.map(this::toTagResponse);
    }
}
