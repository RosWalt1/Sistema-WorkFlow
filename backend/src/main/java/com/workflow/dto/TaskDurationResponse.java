package com.workflow.dto;

import com.workflow.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskDurationResponse {

    private String taskId;
    private String nodeId;
    private String nodeName;
    private String assignedTo;
    private TaskStatus estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFinalizacion;

    private Long durationMinutes;
}