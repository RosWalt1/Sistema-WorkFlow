package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "form_submissions")
public class FormSubmission {

    @Id
    private String id;

    private String activityId;

    private String formId;

    private String userId;

    private String estado;

    private Map<String, Object> respuestas = new HashMap<>();

    private LocalDateTime fechaEnvio;
}