package com.example.demo.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.model.Contact;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    Page<Contact> findByAccountId(Long accountId, Pageable pageable);
}

