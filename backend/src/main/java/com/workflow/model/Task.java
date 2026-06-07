package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class Task {

    @Id
    private String id;

    private String processInstanceId;

    private String policyId;

    private String nodeId;

    private String nodeName;

    private String assignedTo;

    private TaskStatus estado = TaskStatus.PENDIENTE;

    private LocalDateTime fechaCreacion;

    private LocalDateTime fechaFinalizacion;
}