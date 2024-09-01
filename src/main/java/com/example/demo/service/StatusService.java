package com.example.demo.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Pipeline;
import com.example.demo.model.Status;
import com.example.demo.repository.StatusRepository;

@Service
public class StatusService {

    @Autowired
    private StatusRepository statusRepository;

    private final String[] DEFAULT_STATUSES = {"Первичный контакт", "Переговоры", "Принимают решение"};
    public List<Status> createDefaultStatuses(Pipeline pipeline) {
        List<Status> statuses = new ArrayList<Status>();

        for (int i = 0; i < DEFAULT_STATUSES.length; i++) {
            String statusName = DEFAULT_STATUSES[i];
            Status status = new Status(statusName, pipeline, i);
            statuses.add(status);
        }
        statusRepository.saveAll(statuses);
        return statuses;
    }
}
