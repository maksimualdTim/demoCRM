package com.example.demo.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.Contact;
import com.example.demo.model.User;
import com.example.demo.request.ContactCreateRequest;
import com.example.demo.response.ContactResponse;
import com.example.demo.service.UserService;

@Component
public class ContactMapper implements EntityMapper<Contact, ContactResponse, ContactCreateRequest>{

    @Autowired
    private UserService userService;

    @Override
    public ContactResponse toBasicResponse(Contact contact) {
        ContactResponse contactResponse = new ContactResponse();

        contactResponse.setId(contact.getId());

        contactResponse.setCreated_at(contact.getCreatedAt());
        contactResponse.setUpdated_at(contact.getUpdatedAt());
        contactResponse.setName(contact.getContactName());
        contactResponse.setResponsible_id(contact.getResponsible().getId());

        // List<CustomFieldResponse> customFields = Optional.ofNullable(contact.getCustomFields())
        // .orElseGet(Collections::emptyList)
        // .stream()
        // .map(field -> new CustomFieldResponse(
        //     field.getId(),
        //     field.getName(),
        //     field.getType(),
        //     field.getValue()
        // ))
        // .collect(Collectors.toList());

        // contactResponse.setCustom_fields_values(customFields);

        // contactResponse.setLeads(null);

        return contactResponse;        
    }

    @Override
    public List<ContactResponse> toBasicResponse(List<Contact> contacts) {
        if(contacts == null || contacts.isEmpty()) return Collections.emptyList();
        return contacts.stream().map((contact) -> {return this.toBasicResponse(contact);}).collect(Collectors.toList());
    }

    @Override
    public Contact toModel(ContactCreateRequest dto) {
        Contact contact = new Contact();

        contact.setContactName(dto.getName());
        User currentUser = userService.getCurrentUser();
        contact.setAccount(currentUser.getAccount());

        if(dto.getResponsible_id() != null) {
            User responsible = new User();
            responsible.setId(dto.getResponsible_id());
            contact.setResponsible(responsible);
        } else {
            contact.setResponsible(currentUser);
        }


        return contact;
    }
    
}
