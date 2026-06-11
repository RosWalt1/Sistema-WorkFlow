package com.workflow.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "policy_versions")
public class PolicyVersion {

    @Id
    private String id;

    private String policyId;
    private Integer versionNumber;
    private DiagramJson diagramSnapshot;
    private String createdBy;
    private LocalDateTime createdAt;
    private String description;
}