package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.PagedModel;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.User;
import com.example.demo.response.TagResponse;
import com.example.demo.service.TagService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private UserService userService;

    @Autowired
    private TagService tagService;


    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<TagResponse>>> getAllTags(
        Pageable pageable,
        PagedResourcesAssembler<TagResponse> assembler
    ) {
        if (pageable.getPageSize() > 250) {
            pageable = PageRequest.of(pageable.getPageNumber(), 250, pageable.getSort());
        } 

        User user = userService.getCurrentUser();

        Page<TagResponse> tags = tagService.getAllTagsByAccount(user.getAccount().getId(), pageable);
        return ResponseEntity.ok(assembler.toModel(tags));
    }
}
