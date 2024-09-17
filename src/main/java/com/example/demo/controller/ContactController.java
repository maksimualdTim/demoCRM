package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.demo.mapper.ContactMapper;
import com.example.demo.model.Contact;
import com.example.demo.request.ContactCreateRequest;
import com.example.demo.response.ContactResponse;
import com.example.demo.service.ContactService;

import jakarta.validation.Valid;

import java.util.Optional;

@RestController
@RequestMapping("/api/contacts")
public class ContactController {
    
    @Autowired
    private ContactService contactService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<ContactResponse>>> getAllContactsByAccount(
        Pageable pageable,
        PagedResourcesAssembler<ContactResponse> assembler
    ) {
        if (pageable.getPageSize() > 250) {
            pageable = PageRequest.of(pageable.getPageNumber(), 250, pageable.getSort());
        }

        Page<ContactResponse> contacts = contactService.getAllContactsForCurrentUser(pageable);
        return ResponseEntity.ok(assembler.toModel(contacts));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactResponse> getContactById(@PathVariable Long id) {
        Optional<Contact> contactOptional = contactService.getContactById(id);
        if(contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            return ResponseEntity.ok(contactService.toContactResponse(contact));
        }

        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<ContactResponse> createContact(@Valid @RequestBody ContactCreateRequest contactDto) {
        Contact createdContact = contactService.createContact(contactDto);
        return ResponseEntity.ok(contactService.toContactResponse(createdContact));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.noContent().build();
    }
}

