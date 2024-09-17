package com.example.demo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.Lead;
import com.example.demo.model.User;
import com.example.demo.request.LeadCreateRequest;
import com.example.demo.response.LeadResponse;
import com.example.demo.service.UserService;

@Component
public class LeadMapper implements EntityMapper<Lead, LeadResponse, LeadCreateRequest> {
    @Autowired
    private UserService userService;

    @Override
    public LeadResponse toBasicResponse(Lead lead) {
        LeadResponse responseLead = new LeadResponse();
        responseLead.setAccount_id(lead.getAccount().getId());
        responseLead.setId(lead.getId());
        responseLead.setName(lead.getName());
        responseLead.setCreated_at(lead.getCreatedAt());
        responseLead.setUpdated_at(lead.getUpdatedAt());
        responseLead.setResponsible_id(lead.getResponsible().getId());
        responseLead.setStatus_id(lead.getStatus().getId());
        responseLead.setPipeline_id(lead.getPipeline().getId());
        responseLead.setPrice(lead.getPrice());
        // responseLead.setTags(lead.getTags().stream().map(tagService::toTagResponse).collect(Collectors.toSet()));

        // CompanyResponse companyResponse = companyService.toCompanyResponse(lead.getCompany());
        // companyResponse.setShowLeads(false);
        // responseLead.setCompany(companyResponse);

        // List<Contact> contacts = lead.getContacts();
        // List<ContactResponse> contactResponses = contacts
        // .stream()
        // .map(contactService::toContactResponse)
        // .peek(contactResponse -> contactResponse.setShowLeads(false))
        // .collect(Collectors.toList());

        // responseLead.setContacts(contactResponses);

        // List<CustomFieldResponse> customFields = Optional.ofNullable(lead.getCustomFields())
        // .orElseGet(Collections::emptyList)
        // .stream()
        // .map(field -> new CustomFieldResponse(
        //     field.getId(),
        //     field.getName(),
        //     field.getType(),
        //     field.getValue()
        // ))
        // .collect(Collectors.toList());

        // responseLead.setCustom_fields_values(customFields);

        return responseLead;
    }

    @Override
    public List<LeadResponse> toBasicResponse(List<Lead> leads)  {
        if(leads == null || leads.isEmpty()) return Collections.emptyList();
        return leads.stream().map((lead) -> {return this.toBasicResponse(lead);}).collect(Collectors.toList());
    }

    public Lead toModel(LeadCreateRequest dto) {
        Lead lead = new Lead();

        User user = userService.getCurrentUser();
        
        lead.setName(dto.getName());
        lead.setAccount(user.getAccount());
        lead.setPrice(dto.getPrice());

        if(dto.getResponsible_id() != null) {
            User responsible = new User();
            responsible.setId(dto.getResponsible_id());
            lead.setResponsible(responsible);
        } else {
            lead.setResponsible(user);
        }
        return lead;
    }
}
