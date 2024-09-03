package com.example.demo.response;

import org.springframework.hateoas.server.core.Relation;

import lombok.Data;

@Data
@Relation(collectionRelation = "tags")
public class TagResponse {
    private Long id;
    private String name;
}
