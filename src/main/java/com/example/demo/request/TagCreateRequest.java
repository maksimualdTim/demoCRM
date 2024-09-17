package com.example.demo.request;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;

@Data
public class TagCreateRequest {
    private String name;

    private Long id;

    @JsonIgnore
    private String entityType;
    @JsonIgnore
    private Long entityId;
}
