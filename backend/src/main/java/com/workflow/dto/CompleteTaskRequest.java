package com.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompleteTaskRequest {

    private String userId;

    private Map<String, Object> formData = new HashMap<>();

    private Map<String, Object> respuestas = new HashMap<>();
}