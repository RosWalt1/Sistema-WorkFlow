package com.workflow.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "policy_comments")
public class PolicyComment {

    @Id
    private String id;

    private String policyId;
    private String nodeId;
    private String message;
    private String createdBy;
    private LocalDateTime createdAt;
    private boolean resolved = false;
}