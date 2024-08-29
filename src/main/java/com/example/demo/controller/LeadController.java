package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.model.Lead;
import com.example.demo.model.User;
import com.example.demo.request.CreateLeadRequest;
import com.example.demo.response.ResponseLead;
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

    @GetMapping
    public ResponseEntity<Page<Lead>> getAllLeadsByAccount(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size) {
        Pageable pageable = PageRequest.of(page, size);
        User user = userService.getCurrentUser();

        Page<Lead> leads = leadService.getAllLeadsByAccount(user.getAccount().getId(), pageable);
        return ResponseEntity.ok(leads);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseLead> getLeadById(@PathVariable Long id) {
        Optional<Lead> leadOptional = leadService.getLeadById(id);
        if(leadOptional.isPresent()) {
            Lead lead = leadOptional.get();
            return ResponseEntity.ok(leadService.toResponseLead(lead));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Lead> createLead(@Valid @RequestBody CreateLeadRequest leadDto) {        
        Lead lead = leadService.toLead(leadDto);
        Lead createdLead = leadService.createLead(lead);
        return ResponseEntity.ok(createdLead);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLead(@PathVariable Long id) {
        leadService.deleteLead(id);
        return ResponseEntity.noContent().build();
    }
}

