package com.example.demo.request;

import lombok.Data;

@Data
public class CustomFieldCreateRequest {
    private String name;
    private String type;
    private String value;
}
