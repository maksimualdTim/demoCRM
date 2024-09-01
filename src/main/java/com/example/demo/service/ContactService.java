package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.model.Contact;
import com.example.demo.model.User;
import com.example.demo.repository.ContactRepository;
import com.example.demo.request.ContactCreateRequest;
import com.example.demo.response.ContactResponse;
import com.example.demo.response.CustomFieldResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;

    public Page<ContactResponse> getAllContactsByAccount(Long accountId, Pageable pageable) {
        Page<Contact> contacts = contactRepository.findByAccountId(accountId, pageable);
        return contacts.map(this::toContactResponse);
    }

    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Transactional
    public List<Contact> saveAllContacts(List<ContactCreateRequest> contactCreateRequests) {
        List<Contact> contacts = contactCreateRequests.stream()
            .map(this::toContact) // Преобразуем DTO в сущность
            .toList();

        return contactRepository.saveAll(contacts); // Пакетное сохранение
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    public Contact toContact(ContactCreateRequest contactDto) {
        Contact contact = new Contact();

        contact.setContactName(contactDto.getName());
        User currentUser = userService.getCurrentUser();
        contact.setAccount(currentUser.getAccount());

        if(contactDto.getResponsible_id() != null) {
            User responsible = new User();
            responsible.setId(contactDto.getResponsible_id());
            contact.setResponsible(responsible);
        } else {
            contact.setResponsible(currentUser);
        }


        return contact;
    }
    public ContactResponse toContactResponse(Contact contact) {
        ContactResponse contactResponse = new ContactResponse();

        contactResponse.setId(contact.getId());

        contactResponse.setCreated_at(contact.getCreatedAt());
        contactResponse.setUpdated_at(contact.getUpdatedAt());
        contactResponse.setName(contact.getContactName());
        contactResponse.setResponsible_id(contact.getResponsible().getId());

        List<CustomFieldResponse> customFields = Optional.ofNullable(contact.getCustomFields())
        .orElseGet(Collections::emptyList)
        .stream()
        .map(field -> new CustomFieldResponse(
            field.getId(),
            field.getName(),
            field.getType(),
            field.getValue()
        ))
        .collect(Collectors.toList());

        contactResponse.setCustom_fields_values(customFields);

        return contactResponse;
    }
}

