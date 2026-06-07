package com.workflow.dto;

import com.workflow.model.ProcessStatus;
import com.workflow.model.WorkflowHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessMonitoringDetailResponse {

    private String processInstanceId;
    private String policyId;
    private String clienteId;

    private String currentNodeId;
    private String currentNodeName;
    private ProcessStatus estado;

    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    private Long totalDurationMinutes;
    private Double progressPercentage;

    private long totalTasks;
    private long completedTasks;
    private long pendingTasks;
    private long cancelledTasks;

    private List<WorkflowHistory> historial;
    private List<TaskDurationResponse> taskDurations;
}