package com.example.demo.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.hateoas.server.core.Relation;

import lombok.Data;

@Data
@Relation(collectionRelation = "leads")
public class LeadResponse {
    private Long id;
    private String name;
    private Integer price;
    private Long responsible_id;
    private Long status_id;
    private Long pipeline_id;
    private Date created_at;
    private Date updated_at;
    private Long account_id;

    private List<ContactResponse> contacts;
    private CompanyResponse company;
    private Set<TagResponse> tags;

    private List<CustomFieldResponse> custom_fields_values;

    public List<ContactResponse> getContacts() {
        if(contacts == null)
            contacts = new ArrayList<ContactResponse>();
        return contacts;
    }

    public Set<TagResponse> getTags() {
        if(tags == null)
            tags = new HashSet<TagResponse>();
        return tags;
    }
}
