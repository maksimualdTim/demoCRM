package com.example.demo.response;

import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class ResponseLead {
    private Long id;
    private String name;
    private Integer price;
    private Long responsible_id;
    private Long status_id;
    private Long pipeline_id;
    private Date created_at;
    private Date updated_at;
    private Long account_id;

    private List<CustomFieldResponse> custom_fields_values;
}
