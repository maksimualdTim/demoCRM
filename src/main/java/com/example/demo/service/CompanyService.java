package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.example.demo.model.Company;
import com.example.demo.model.User;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.response.CompanyResponse;
import com.example.demo.response.CustomFieldResponse;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

@Service
public class CompanyService {

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private UserService userService;

    public Page<CompanyResponse> getAllCompaniesByAccount(Long accountId, Pageable pageable) {
        Page<Company> companies = companyRepository.findByAccountId(accountId, pageable);

        return companies.map(this::toCompanyResponse);
    }

    public Optional<Company> getCompanyById(Long id) {
        return companyRepository.findById(id);
    }

    public Company createCompany(Company company) {
        return companyRepository.save(company);
    }

    public void deleteCompany(Long id) {
        companyRepository.deleteById(id);
    }

    public CompanyResponse toCompanyResponse(Company company) {
        CompanyResponse companyResponse = new CompanyResponse();
        companyResponse.setCreated_at(company.getCreatedAt());
        companyResponse.setUpdated_at(company.getUpdatedAt());
        companyResponse.setName(company.getCompanyName());
        companyResponse.setResponsible_id(company.getResponsible().getId());
        companyResponse.setId(company.getId());

        List<CustomFieldResponse> customFields = Optional.ofNullable(company.getCustomFields())
        .orElseGet(Collections::emptyList)
        .stream()
        .map(field -> new CustomFieldResponse(
            field.getId(),
            field.getName(),
            field.getType(),
            field.getValue()
        ))
        .collect(Collectors.toList());
        
        companyResponse.setCustom_fields_values(customFields);
        return companyResponse;
    }

    public Company toCompany(CompanyCreateRequest companyCreateRequest) {
        Company company = new Company();
        company.setCompanyName(companyCreateRequest.getName());
        User currentUser = userService.getCurrentUser();
        company.setAccount(currentUser.getAccount());

        if(companyCreateRequest.getResponsible_id() != null) {
            User responsible = new User();
            responsible.setId(companyCreateRequest.getResponsible_id());
            company.setResponsible(responsible);
        } else {
            company.setResponsible(currentUser);
        }

        return company;
    }
}

