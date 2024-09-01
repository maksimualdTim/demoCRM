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
import com.example.demo.model.User;
import com.example.demo.request.LeadCreateRequest;
import com.example.demo.response.CompanyResponse;
import com.example.demo.response.ContactResponse;
import com.example.demo.response.LeadResponse;
import com.example.demo.service.CompanyService;
import com.example.demo.service.ContactService;
import com.example.demo.service.LeadService;
import com.example.demo.service.UserService;

import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api/leads")
public class LeadController {

    @Autowired
    private LeadService leadService;


    @Autowired
    UserService userService;

    @Autowired
    ContactService contactService;

    @Autowired
    CompanyService companyService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<LeadResponse>>> getAllLeadsByAccount(
        Pageable pageable,
        PagedResourcesAssembler<LeadResponse> assembler) {

        if (pageable.getPageSize() > 250) {
            pageable = PageRequest.of(pageable.getPageNumber(), 250, pageable.getSort());
        }    

        User user = userService.getCurrentUser();

        Page<LeadResponse> leads = leadService.getAllLeadsByAccount(user.getAccount().getId(), pageable);
        
        return ResponseEntity.ok(assembler.toModel(leads));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LeadResponse> getLeadById(@PathVariable Long id) {
        Optional<Lead> leadOptional = leadService.getLeadById(id);
        if(leadOptional.isPresent()) {
            Lead lead = leadOptional.get();
            return ResponseEntity.ok(leadService.toLeadResponse(lead));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<EntityModel<LeadResponse>> createLead(@Valid @RequestBody LeadCreateRequest leadCreateRequest) {
        Lead lead = leadService.createLead(leadService.toLead(leadCreateRequest));

        // Создание EntityModel для возвращаемого объекта Lead
        EntityModel<LeadResponse> leadResource = EntityModel.of(leadService.toLeadResponse(lead));
        leadResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(LeadController.class).createLead(leadCreateRequest)).withSelfRel());

        // Добавление ссылок на связанные сущности
        if (lead.getCompany() != null) {
            CompanyResponse companyResponse = companyService.toCompanyResponse(lead.getCompany());
            companyResponse.setShowLeads(false);
            EntityModel<CompanyResponse> companyResource = EntityModel.of(companyResponse);
            companyResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CompanyController.class).getCompanyById(lead.getCompany().getId())).withSelfRel());
            leadResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(CompanyController.class).getCompanyById(lead.getCompany().getId())).withRel("company"));
        }

        if (lead.getContacts() != null) {
            for (Contact contact : lead.getContacts()) {
                ContactResponse contactResponse = contactService.toContactResponse(contact);
                contactResponse.setShowLeads(false);
                EntityModel<ContactResponse> contactResource = EntityModel.of(contactResponse);
                contactResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContactController.class).getContactById(contact.getId())).withSelfRel());
                leadResource.add(WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ContactController.class).getContactById(contact.getId())).withRel("contacts"));
            }
        }

        return new ResponseEntity<>(leadResource, HttpStatus.CREATED);
    }
    // @PostMapping
    // public ResponseEntity<LeadResponse> createLead(@Valid @RequestBody LeadCreateRequest leadDto) {        
    //     Lead lead = leadService.toLead(leadDto);
    //     Lead createdLead = leadService.createLead(lead, null, null);
    //     return ResponseEntity.ok(leadService.toLeadResponse(createdLead));
    // }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }
}

