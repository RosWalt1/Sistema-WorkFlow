package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "policy_requirements")
public class PolicyRequirement {

    @Id
    private String id;

    private String policyId;

    private List<String> initialRequiredDocs = new ArrayList<>();

    private List<String> initialQuestions = new ArrayList<>();
}