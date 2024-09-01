package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Account;
import com.example.demo.model.Pipeline;
import com.example.demo.model.User;
import com.example.demo.repository.PipelineRepository;

@Service
public class PipelineService {

    @Autowired
    private PipelineRepository pipelineRepository;

    private final String DEFAULT_PIPILINE_NAME = "Воронка";

    public Pipeline createDefaultPipeline(Account account) {
        Pipeline pipeline = new Pipeline(DEFAULT_PIPILINE_NAME);
        pipeline.setAccount(account);
        return pipelineRepository.save(pipeline);
    }

    public Pipeline getMainPipeline(Account account) {
        return pipelineRepository.findTopByAccountIdOrderByCreatedAtAsc(account.getId());
    }
}
