package com.example.demo.response;

import org.springframework.hateoas.server.core.Relation;

import lombok.Data;

@Data
@Relation(collectionRelation = "pipelines")
public class PipelineResponse {

}
