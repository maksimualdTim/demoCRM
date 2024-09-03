package com.example.demo.mapper;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.Company;
import com.example.demo.model.User;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.response.CompanyResponse;
import com.example.demo.service.UserService;

@Component
public class CompanyMapper implements EntityMapper<Company, CompanyResponse, CompanyCreateRequest>{
    @Autowired
    private UserService userService;

    @Override
    public CompanyResponse toBasicResponse(Company company) {
        CompanyResponse companyResponse = new CompanyResponse();
        companyResponse.setCreated_at(company.getCreatedAt());
        companyResponse.setUpdated_at(company.getUpdatedAt());
        companyResponse.setName(company.getCompanyName());
        companyResponse.setResponsible_id(company.getResponsible().getId());
        companyResponse.setId(company.getId());

        // List<CustomFieldResponse> customFields = Optional.ofNullable(company.getCustomFields())
        // .orElseGet(Collections::emptyList)
        // .stream()
        // .map(field -> new CustomFieldResponse(
        //     field.getId(),
        //     field.getName(),
        //     field.getType(),
        //     field.getValue()
        // ))
        // .collect(Collectors.toList());
        
        // companyResponse.setCustom_fields_values(customFields);
        return companyResponse;
    }

    @Override
    public List<CompanyResponse> toBasicResponse(List<Company> companies) {
        return companies.stream().map((company) -> {return this.toBasicResponse(company);}).collect(Collectors.toList());
    }

    @Override
    public Company toModel(CompanyCreateRequest dto) {
        Company company = new Company();
        company.setCompanyName(dto.getName());
        User currentUser = userService.getCurrentUser();
        company.setAccount(currentUser.getAccount());

        if(dto.getResponsible_id() != null) {
            User responsible = new User();
            responsible.setId(dto.getResponsible_id());
            company.setResponsible(responsible);
        } else {
            company.setResponsible(currentUser);
        }

        return company;
    }

}
