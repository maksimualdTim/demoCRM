package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TagCreateRequest {
    @NotBlank
    private String name;
}
