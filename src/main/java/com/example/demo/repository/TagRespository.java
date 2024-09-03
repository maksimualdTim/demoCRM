package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Account;
import com.example.demo.model.Tag;

public interface TagRespository extends JpaRepository<Tag, Long>{
    Optional<Tag> findByNameAndAccount(String name, Account account);
    Page<Tag> findByAccountId(Long accountId, Pageable pageable);
}
