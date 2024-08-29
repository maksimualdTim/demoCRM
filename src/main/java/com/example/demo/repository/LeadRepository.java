package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Lead;

@Repository
public interface LeadRepository extends JpaRepository<Lead, Long> {
    Page<Lead> findByAccountId(Long accountId, Pageable pageable);
}

