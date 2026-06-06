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
@Document(collection = "task_instances")
public class TaskInstance {

    @Id
    private String id;

    private String processId;

    private String activityId;

    private String responsableId;

    private TaskStatus estado = TaskStatus.PENDIENTE;

    private LocalDateTime fechaAsignacion;

    private LocalDateTime fechaCompletado;
}