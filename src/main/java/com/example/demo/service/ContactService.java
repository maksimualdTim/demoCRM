package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.ContactMapper;
import com.example.demo.mapper.LeadMapper;
import com.example.demo.mapper.TagMapper;
import com.example.demo.model.Contact;
import com.example.demo.model.Tag;
import com.example.demo.repository.ContactRepository;
import com.example.demo.request.ContactCreateRequest;
import com.example.demo.request.TagCreateRequest;
import com.example.demo.response.ContactResponse;
import com.example.demo.response.TagResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TagService tagService;

    public Page<ContactResponse> getAllContactsForCurrentUser(Pageable pageable) {
        Page<Contact> contacts = contactRepository.findByAccountId(userService.getCurrentUser().getAccount().getId(), pageable);
        return contacts.map(this::toContactResponse);
    }

    public Optional<Contact> getContactById(Long id) {
        return contactRepository.findById(id);
    }

    public Contact createContact(ContactCreateRequest contactCreateRequest) {
        Contact contact = contactMapper.toModel(contactCreateRequest);
        contact = createContact(contact);

        if(!contactCreateRequest.getTags().isEmpty()) {
            List<TagCreateRequest> tagsCreate = contactCreateRequest.getTags();
            List<Tag> tags = tagsCreate.stream().map(tagMapper::toModel).collect(Collectors.toList());

            tags = tagService.saveTags(tags);

            tagService.linkTags(tags, contact.getId(), "contacts");
        }

        return contact;
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

        List<Tag> tags = tagService.getTags(contact.getId(), "contacts");

        if(!tags.isEmpty()) {
            contactResponse.setTags(tagMapper.toBasicResponse(tags));
        }

        return contactResponse;
    }
}

