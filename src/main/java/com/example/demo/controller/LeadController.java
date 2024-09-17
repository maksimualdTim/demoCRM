package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Contact;
import com.example.demo.model.Lead;
import com.example.demo.request.LeadCreateRequest;
import com.example.demo.response.LeadResponse;
import com.example.demo.service.LeadService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    @Autowired
    private LeadService leadService;


    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<LeadResponse>>> getAllLeads(
        Pageable pageable,
        PagedResourcesAssembler<LeadResponse> assembler) {

        if (pageable.getPageSize() > 250) {
            pageable = PageRequest.of(pageable.getPageNumber(), 250, pageable.getSort());
        }    

        Page<LeadResponse> leads = leadService.getAllLeadsForCurrentUser(pageable);
        
        return ResponseEntity.ok(assembler.toModel(leads));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable Long id) {
        LeadResponse leadResponse = leadService.getLeadResponseById(id);
        if(leadResponse != null) {
            return ResponseEntity.ok(leadResponse);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EntityModel<LeadResponse>> createLead(@Valid @RequestBody LeadCreateRequest leadCreateRequest) {
        Lead lead = leadService.createLead(leadCreateRequest);

        // Создание EntityModel для возвращаемого объекта Lead
        EntityModel<LeadResponse> leadResource = EntityModel.of(leadService.toLeadResponse(lead));
        leadResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LeadController.class).createLead(leadCreateRequest)).withSelfRel());

        // Добавление ссылок на связанные сущности
        if (lead.getCompany() != null) {
            leadResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CompanyController.class).getCompanyById(lead.getCompany().getId())).withRel("company"));
        }

        if (lead.getContacts() != null) {
            for (Contact contact : lead.getContacts()) {
                leadResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContactController.class).getContactById(contact.getId())).withRel("contacts"));
            }
        }

        return new ResponseEntity<>(leadResource, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }
}

