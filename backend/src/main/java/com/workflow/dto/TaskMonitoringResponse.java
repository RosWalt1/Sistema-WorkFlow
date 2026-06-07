package com.workflow.dto;

import com.workflow.model.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskMonitoringResponse {

    private String taskId;
    private String processInstanceId;
    private String policyId;

    private String nodeId;
    private String nodeName;

    private String assignedTo;
    private TaskStatus estado;

    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaFinalizacion;
}