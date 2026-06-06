package com.workflow.dto;

import com.workflow.model.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskInstanceResponse {

    private String id;

    private String processId;

    private String activityId;

    private String responsableId;

    private TaskStatus estado;

    private LocalDateTime fechaAsignacion;

    private LocalDateTime fechaCompletado;
}