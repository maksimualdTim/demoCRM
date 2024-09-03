package com.example.demo.request;

import java.util.List;
import lombok.Data;

@Data
public class CompanyCreateRequest {
    private String name;
    private Long responsible_id;
    private List<CustomFieldCreateRequest> customFields;
    private List<TagCreateRequest> tags;
}
