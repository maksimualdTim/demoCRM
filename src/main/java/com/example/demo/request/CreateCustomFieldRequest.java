package com.example.demo.request;

import lombok.Data;

@Data
public class CreateCustomFieldRequest {
    private String name;
    private String type;
    private String value;
}
