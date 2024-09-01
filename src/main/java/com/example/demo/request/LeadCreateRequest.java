package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class LeadCreateRequest {
    @NotBlank
    private String name;

    private Integer price;
    private List<ContactCreateRequest> contacts;
    private CompanyCreateRequest company;
    private Long responsible_id;
    private Long pipeline_id;
    private Long status_id;
    private List<CustomFieldCreateRequest> customFields;
}
