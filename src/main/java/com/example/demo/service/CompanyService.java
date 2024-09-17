package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.CompanyMapper;
import com.example.demo.mapper.LeadMapper;
import com.example.demo.mapper.TagMapper;
import com.example.demo.model.Company;
import com.example.demo.model.Tag;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.request.TagCreateRequest;
import com.example.demo.response.CompanyResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CompanyMapper companyMapper;

    @Autowired
    private LeadMapper leadMapper;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private TagService tagService;

    public Page<CompanyResponse> getAllCompaniesForCurrentUser(Pageable pageable) {
        Page<Company> companies = companyRepository.findByAccountId(userService.getCurrentUser().getAccount().getId(), pageable);

        return companies.map(this::toCompanyResponse);
    }

    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Company createCompany(CompanyCreateRequest companyDto) {
        Company company = companyMapper.toModel(companyDto);

        company = createCompany(company);

        if(!companyDto.getTags().isEmpty()) {
            List<TagCreateRequest> tagsCreate = companyDto.getTags();
            List<Tag> tags = tagsCreate.stream().map(tagMapper::toModel).collect(Collectors.toList());

            tags = tagService.saveTags(tags);

            tagService.linkTags(tags, company.getId(), "companies");
        }

        return company;
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public CompanyResponse toCompanyResponse(Company company) {
        CompanyResponse companyResponse = companyMapper.toBasicResponse(company);
        companyResponse.setLeads(leadMapper.toBasicResponse(company.getLeads()));

        List<Tag> tags = tagService.getTags(company.getId(), "companies");

        if(!tags.isEmpty()) {
            companyResponse.setTags(tagMapper.toBasicResponse(tags));
        }
        return companyResponse;
    }
}

