package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.mapper.CompanyMapper;
import com.example.demo.mapper.ContactMapper;
import com.example.demo.mapper.LeadMapper;
import com.example.demo.mapper.TagMapper;
import com.example.demo.model.Company;
import com.example.demo.model.Contact;
import com.example.demo.model.Lead;
import com.example.demo.model.Pipeline;
import com.example.demo.model.Tag;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.repository.ContactRepository;
import com.example.demo.repository.LeadRepository;
import com.example.demo.request.ContactCreateRequest;
import com.example.demo.request.LeadCreateRequest;
import com.example.demo.request.TagCreateRequest;
import com.example.demo.response.CompanyResponse;
import com.example.demo.response.ContactResponse;
import com.example.demo.response.LeadResponse;

import java.util.Optional;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class LeadService {

    @Autowired
    private LeadRepository leadRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private ContactService contactService;

    @Autowired 
    private CompanyService companyService;

    @Autowired
    private ContactRepository contactRepository;

    @Autowired 
    private CompanyRepository companyRepository;

    @Autowired
    private LeadMapper leadMapper;

    @Autowired
    private ContactMapper contactMapper;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TagService tagService;

    public Page<LeadResponse> getAllLeadsForCurrentUser(Pageable pageable) {
        Page<Lead> leads = leadRepository.findByAccountId(userService.getCurrentUser().getAccount().getId(), pageable);
        return leads.map(this::toLeadResponse);
    }

    public LeadResponse getLeadResponseById(Long id) {
        Optional<Lead> leadOptional = leadRepository.findById(id);
        if (!leadOptional.isPresent())
            return null;

        Lead lead = leadOptional.get();
        return toLeadResponse(lead);
    }

    @Transactional
    public Lead createLead(Lead lead) {
        if(lead.getPipeline() == null) {
            Pipeline pipeline = pipelineService.getMainPipeline(userService.getCurrentUser().getAccount());
            lead.setPipeline(pipeline);
            lead.setStatus(pipeline.getStatuses().getFirst());
        }

        //TODO move to proper services
        if(lead.getCompany() != null) {
            
            Company company = lead.getCompany();
            List<Lead> companyLeads = company.getLeads();
            if(companyLeads == null) {
                companyLeads = new ArrayList<Lead>();
            }
            companyLeads.add(lead);
            company.setLeads(companyLeads);
            company = companyRepository.save(company);
        }

        if(lead.getContacts() != null) {
            List<Contact> contacts = lead.getContacts();
            for (Contact contact : contacts) {
                List<Lead> contactLeads = contact.getLeads();
                if(contactLeads == null) {
                    contactLeads = new ArrayList<Lead>();
                }
                contactLeads.add(lead);
                contact.setLeads(contactLeads);
            }
            contacts = contactRepository.saveAll(contacts);
        }

        return leadRepository.save(lead);
    }

    public void deleteLead(Long id) {
        leadRepository.deleteById(id);
    }

    public Lead createLead(LeadCreateRequest leadDto) {
        Lead lead = leadMapper.toModel(leadDto);

        if(leadDto.getCompany() != null) {
            Company company = companyService.createCompany(companyMapper.toModel(leadDto.getCompany()));
            lead.setCompany(company);
        }

        if(leadDto.getContacts() != null) {
            List<ContactCreateRequest> contacts = leadDto.getContacts();
            lead.setContacts(contactService.saveAllContacts(contacts.stream().map(contactMapper::toModel).collect(Collectors.toList())));
        }

        lead = createLead(lead);

        if(leadDto.getTags() != null) {
            List<TagCreateRequest> tagsCreate = leadDto.getTags();
            List<Tag> tags = tagsCreate.stream().map(tagMapper::toModel).collect(Collectors.toList());
            
            tags = tagService.saveTags(tags);

            tagService.linkTags(tags, lead.getId(), "leads");
        }

        return lead;
    }

    // public LeadResponse toLeadResponse(Lead lead) {
    //     LeadResponse responseLead = new LeadResponse();
    //     responseLead.setAccount_id(lead.getAccount().getId());
    //     responseLead.setId(lead.getId());
    //     responseLead.setName(lead.getName());
    //     responseLead.setCreated_at(lead.getCreatedAt());
    //     responseLead.setUpdated_at(lead.getUpdatedAt());
    //     responseLead.setResponsible_id(lead.getResponsible().getId());
    //     responseLead.setStatus_id(lead.getStatus().getId());
    //     responseLead.setPipeline_id(lead.getPipeline().getId());
    //     responseLead.setPrice(lead.getPrice());
    //     responseLead.setTags(lead.getTags().stream().map(tagService::toTagResponse).collect(Collectors.toSet()));

    //     CompanyResponse companyResponse = companyService.toCompanyResponse(lead.getCompany());
    //     companyResponse.setShowLeads(false);
    //     responseLead.setCompany(companyResponse);

    //     List<Contact> contacts = lead.getContacts();
    //     List<ContactResponse> contactResponses = contacts
    //     .stream()
    //     .map(contactService::toContactResponse)
    //     .peek(contactResponse -> contactResponse.setShowLeads(false))
    //     .collect(Collectors.toList());

    //     responseLead.setContacts(contactResponses);

    //     List<CustomFieldResponse> customFields = Optional.ofNullable(lead.getCustomFields())
    //     .orElseGet(Collections::emptyList)
    //     .stream()
    //     .map(field -> new CustomFieldResponse(
    //         field.getId(),
    //         field.getName(),
    //         field.getType(),
    //         field.getValue()
    //     ))
    //     .collect(Collectors.toList());

    //     responseLead.setCustom_fields_values(customFields);

    //     return responseLead;
    // }

    public LeadResponse toLeadResponse (Lead lead) {
        LeadResponse leadResponse = leadMapper.toBasicResponse(lead);

        Company company = lead.getCompany();
        if(company != null) {
            CompanyResponse companyResponse = companyMapper.toBasicResponse(company);
            companyResponse.setShowLeads(false);
            List<Tag> tagsCompany = tagService.getTags(company.getId(), "company");
    
            if(!tagsCompany.isEmpty()) {
                companyResponse.setTags(tagMapper.toBasicResponse(tagsCompany));
            }

            leadResponse.setCompany(companyResponse);
        }

        List<Contact> contacts = lead.getContacts();

        if(!contacts.isEmpty()) {
            List<ContactResponse> contactResponses = contactMapper.toBasicResponse(contacts);

            List<Long> contactIds = contacts.stream()
            .map(Contact::getId)
            .collect(Collectors.toList());
            
            Map<Long, List<Tag>> contactTagsMap = tagService.getTags(contactIds, "contacts");

            contactResponses.forEach(contactResponse -> {
                Long contactId = contactResponse.getId();
                List<Tag> tags = contactTagsMap.getOrDefault(contactId, Collections.emptyList());
                contactResponse.setTags(tagMapper.toBasicResponse(tags));
                contactResponse.setShowLeads(false);
            });

            leadResponse.setContacts(contactResponses);
        }


        List<Tag> tags = tagService.getTags(lead.getId(), "leads");
        if(!tags.isEmpty()) {
            leadResponse.setTags(tagMapper.toBasicResponse(tags));
        }

        return leadResponse;
    }
}

