package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Account;
import com.example.demo.model.Pipeline;
import java.util.List;


public interface PipelineRepository extends JpaRepository<Pipeline, Long>{
    Pipeline findTopByAccountIdOrderByCreatedAtAsc(Long accountId);

    List<Pipeline> findByAccount(Account account);
}
