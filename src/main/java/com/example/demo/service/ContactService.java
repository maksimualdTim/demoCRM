package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.ContactMapper;
import com.example.demo.mapper.LeadMapper;
import com.example.demo.model.Contact;
import com.example.demo.repository.ContactRepository;
import com.example.demo.response.ContactResponse;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private LeadMapper leadMapper;

    public Page<ContactResponse> getAllContactsForCurrentUser(Pageable pageable) {
        Page<Contact> contacts = contactRepository.findByAccountId(userService.getCurrentUser().getAccount().getId(), pageable);
        return contacts.map(this::toContactResponse);
    }

    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact createContact(Contact contact) {
        return contactRepository.save(contact);
    }

    @Transactional
    public List<Contact> saveAllContacts(List<Contact> contacts) {
        return contactRepository.saveAll(contacts);
    }

    public void deleteContact(Long id) {
        contactRepository.deleteById(id);
    }

    public ContactResponse toContactResponse(Contact contact) {
        ContactResponse contactResponse = contactMapper.toBasicResponse(contact);
        contactResponse.setLeads(leadMapper.toBasicResponse(contact.getLeads()));
        return contactResponse;
    }
}

