package com.example.demo.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Pipeline;
import com.example.demo.service.PipelineService;
import com.example.demo.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("/api/pipelines")
public class PipelineController {

    @Autowired
    private PipelineService pipelineService;

    @Autowired
    private UserService userService;

    @GetMapping
    public List<Pipeline> getAllPipelines() {
        return pipelineService.getAllPipelines(userService.getCurrentUser().getAccount());
    }
}
