package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.TagAssignment;

public interface TagAssignmentRepository extends JpaRepository<TagAssignment, Long>{
    List<TagAssignment> findByEntityIdAndEntityType(Long entityId, String entityType);
    List<TagAssignment> findByEntityIdInAndEntityType(List<Long> entityIds, String entityType);
}
