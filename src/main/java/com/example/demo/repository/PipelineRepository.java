package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Pipeline;

public interface PipelineRepository extends JpaRepository<Pipeline, Long>{
    Pipeline findTopByAccountIdOrderByCreatedAtAsc(Long accountId);
}
