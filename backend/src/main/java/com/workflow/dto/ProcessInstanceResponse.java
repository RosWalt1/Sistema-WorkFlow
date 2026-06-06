package com.workflow.dto;

import com.workflow.model.ProcessStatus;
import com.workflow.model.WorkflowHistory;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProcessInstanceResponse {

    private String id;

    private String policyId;

    private ProcessStatus estado;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private List<WorkflowHistory> historial;
}