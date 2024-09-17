package com.example.demo.response;

import lombok.Data;
import java.util.List;

import org.springframework.hateoas.server.core.Relation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.Date;
import java.util.ArrayList;

@Data
@Relation(collectionRelation = "companies")
public class CompanyResponse {

    @JsonIgnore
    private boolean isShowLeads = true;

    private Long id;
    private String name;
    private Long responsible_id;
    @JsonInclude(Include.NON_NULL)
    private List<LeadResponse> leads; 
    private List<TagResponse> tags;
    private Date created_at;
    private Date updated_at;
    private List<CustomFieldResponse> custom_fields_values;

    public List<LeadResponse> getLeads() {
        if(isShowLeads) {
            if(leads == null)
                leads = new ArrayList<LeadResponse>();
            return leads;
        }
        return null;
    }

    public List<TagResponse> getTags() {
        if(tags == null)
            tags = new ArrayList<TagResponse>();
        return tags;
    }
}
