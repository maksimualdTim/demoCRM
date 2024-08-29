package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.model.Lead;
import com.example.demo.model.User;
import com.example.demo.repository.LeadRepository;
import com.example.demo.request.CreateLeadRequest;
import com.example.demo.response.CustomFieldResponse;
import com.example.demo.response.ResponseLead;

import java.util.Optional;
import java.util.ArrayList;
import java.util.List;
@Service
public class LeadService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private UserService userService;

    public Page<Lead> getAllLeadsByAccount(Long accountId, Pageable pageable) {
        return leadRepository.findByAccountId(accountId, pageable);
    }

    public Optional<Lead> getLeadById(Long id) {
        return leadRepository.findById(id);
    }

    public Lead createLead(Lead lead) {
        return leadRepository.save(lead);
    }

    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }

    public Lead toLead(CreateLeadRequest leadDto) {
        Lead lead = new Lead();

        User user = userService.getCurrentUser();
        
        lead.setName(leadDto.getName());
        lead.setAccount(user.getAccount());

        if(leadDto.getResponsible_id() != null) {
            User responsible = new User();
            responsible.setId(leadDto.getResponsible_id());
            lead.setResponsible(responsible);
        } else {
            lead.setResponsible(userService.getCurrentUser());
        }

        return lead;
    }

    public ResponseLead toResponseLead(Lead lead) {
        ResponseLead responseLead = new ResponseLead();
        responseLead.setAccount_id(lead.getAccount().getId());
        responseLead.setId(lead.getId());
        responseLead.setName(lead.getName());
        responseLead.setCreated_at(lead.getCreatedAt());
        responseLead.setUpdated_at(lead.getUpdatedAt());
        responseLead.setResponsible_id(lead.getResponsible().getId());
        responseLead.setStatus_id(lead.getStatus().getId());
        responseLead.setPipeline_id(lead.getPipeline().getId());

        List<CustomFieldResponse> customFields = lead.getCustomFields().stream()
        .map(field -> new CustomFieldResponse(
            field.getId(),
            field.getName(),
            field.getType(),
            field.getValue()
        ))
        .toList();

        responseLead.setCustom_fields_values(customFields);

        return responseLead;
    }

    public List<ResponseLead> getLeadsResponse(Pageable pageable) {
        List<ResponseLead> list = new ArrayList<ResponseLead>();

        Page<Lead> leads = getAllLeadsByAccount(userService.getCurrentUser().getAccount().getId(), pageable);

        for (Lead lead : leads) {
            list.add(toResponseLead(lead));
        }

        return list;
    }
}

