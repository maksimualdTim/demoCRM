package com.example.demo.response;

import org.springframework.hateoas.server.core.Relation;

import com.example.demo.model.Lead;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;
import java.util.ArrayList;
import lombok.Data;
import java.util.Date;

@Data
@Relation(collectionRelation = "contacts")
public class ContactResponse {

    @JsonIgnore
    private boolean isShowLeads = true;

    private Long id;
    private String name;
    private Long responsible_id;
    @JsonInclude(Include.NON_NULL)
    private List<Lead> leads;
    private Date created_at;
    private Date updated_at;
    private List<CustomFieldResponse> custom_fields_values;

    public List<Lead> getLeads() {
        if(isShowLeads) {
            if(leads == null)
                leads = new ArrayList<Lead>();
            return leads;
        }
        return null;
    }
}
