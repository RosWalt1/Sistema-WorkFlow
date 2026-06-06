package com.workflow.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class CompleteTaskRequest {

    private String userId;

    private String formId;

    private Map<String, Object> respuestas = new HashMap<>();
}