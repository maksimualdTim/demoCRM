package com.example.demo.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.model.Account;
import com.example.demo.model.Tag;
import com.example.demo.model.TagAssignment;
import com.example.demo.repository.TagAssignmentRepository;
import com.example.demo.repository.TagRepository;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagAssignmentRepository tagAssignmentRepository;


    public List<Tag> saveTags(List<Tag> tags) {
        if (tags.isEmpty()) {
            return Collections.emptyList();
        }

        Account account = tags.get(0).getAccount();
        List<String> tagNames = tags.stream()
                                    .map(Tag::getName)
                                    .collect(Collectors.toList());

        List<Tag> existingTags = tagRepository.findByNameInAndAccount(tagNames, account);

        Set<String> existingTagNames = existingTags.stream()
                                                   .map(Tag::getName)
                                                   .collect(Collectors.toSet());

        List<Tag> newTags = tags.stream()
                                .filter(tag -> !existingTagNames.contains(tag.getName()))
                                .collect(Collectors.toList());
        
        if (!newTags.isEmpty()) {
            newTags = tagRepository.saveAll(newTags);
        }
        
        List<Tag> allTags = new ArrayList<>(existingTags);
        allTags.addAll(newTags);
        return allTags;
    }

    public void linkTags(List<Tag> tags, Long entityId, String entityType) {
        List<TagAssignment> tagAssignments = new ArrayList<>();
        
        for (Tag tag : tags) {
            TagAssignment tagAssignment = new TagAssignment();
            tagAssignment.setTag(tag);
            tagAssignment.setEntityType(entityType);
            tagAssignment.setEntityId(entityId);
            tagAssignments.add(tagAssignment);
        }
        tagAssignments = tagAssignmentRepository.saveAll(tagAssignments);
    }

    public List<Tag> getTags(Long entityId, String entityType) {
        List<TagAssignment> tagAssignments = tagAssignmentRepository.findByEntityIdAndEntityType(entityId, entityType);

        if (tagAssignments == null || tagAssignments.isEmpty()) {
            return Collections.emptyList();
        }

        return tagAssignments.stream()
        .map(TagAssignment::getTag)  
        .collect(Collectors.toList());
    }

    public Map<Long, List<Tag>> getTags(List<Long> entityIds, String entityType) {
        List<TagAssignment> tagAssignments = tagAssignmentRepository.findByEntityIdInAndEntityType(entityIds, entityType);
        
        Map<Long, List<Tag>> contactTagsMap = tagAssignments.stream()
        .collect(Collectors.groupingBy(
            TagAssignment::getEntityId,
            Collectors.mapping(TagAssignment::getTag, Collectors.toList())
        ));

        return contactTagsMap;
    }
}
