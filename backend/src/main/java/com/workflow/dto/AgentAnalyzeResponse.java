package com.workflow.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AgentAnalyzeResponse {

    private String mensajeOriginal;
    private String intention;
    private String recommendedPolicyId;
    private String recommendedPolicyName;
    private String descripcion;
    private List<String> requiredDocuments = new ArrayList<>();
    private String estimatedDuration;
    private String recommendedFor;
    private Integer score;
    private boolean found;
}