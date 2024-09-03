package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.CompanyMapper;
import com.example.demo.mapper.LeadMapper;
import com.example.demo.model.Company;
import com.example.demo.repository.CompanyRepository;
import com.example.demo.response.CompanyResponse;

import java.util.Optional;

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

    public Page<CompanyResponse> getAllCompaniesForCurrentUser(Pageable pageable) {
        Page<Company> companies = companyRepository.findByAccountId(userService.getCurrentUser().getAccount().getId(), pageable);

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
        CompanyResponse companyResponse = companyMapper.toBasicResponse(company);
        companyResponse.setLeads(leadMapper.toBasicResponse(company.getLeads()));
        return companyResponse;
    }
}

