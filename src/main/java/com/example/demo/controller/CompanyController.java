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
import com.example.demo.model.Company;
import com.example.demo.model.User;
import com.example.demo.request.CompanyCreateRequest;
import com.example.demo.response.CompanyResponse;
import com.example.demo.service.CompanyService;
import com.example.demo.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    UserService userService;

    @GetMapping
    public ResponseEntity<PagedModel<EntityModel<CompanyResponse>>> getAllCompaniesByAccount(
        Pageable pageable,
        PagedResourcesAssembler<CompanyResponse> assembler
    ) {
        if (pageable.getPageSize() > 250) {
            pageable = PageRequest.of(pageable.getPageNumber(), 250, pageable.getSort());
        }

        User user = userService.getCurrentUser();

        Page<CompanyResponse> companies = companyService.getAllCompaniesByAccount(user.getAccount().getId(), pageable);
        return ResponseEntity.ok(assembler.toModel(companies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompanyById(@PathVariable Long id) {
        Optional<Company> companyOptional = companyService.getCompanyById(id);
        if(companyOptional.isPresent()) {
            Company company = companyOptional.get();
            return ResponseEntity.ok(companyService.toCompanyResponse(company));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Company> createCompany(@RequestBody CompanyCreateRequest companyDto) {
        Company company = companyService.toCompany(companyDto);
        Company createdCompany = companyService.createCompany(company);
        return ResponseEntity.ok(createdCompany);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable Long id) {
        companyService.deleteCompany(id);
        return ResponseEntity.noContent().build();
    }
}

