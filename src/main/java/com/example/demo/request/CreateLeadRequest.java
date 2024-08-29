package com.example.demo.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.util.List;

@Data
public class CreateLeadRequest {
    @NotBlank
    private String name;

    private Long responsible_id;
    private Long pipeline_id;
    private Long status_id;
    private List<CreateCustomFieldRequest> customFields;
}
