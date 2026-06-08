package com.workflow.dto;

import com.workflow.model.ProcessStatus;
import com.workflow.model.RiskSeverity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessRiskResponse {

    private String processId;
    private String policyId;
    private String clienteId;

    private ProcessStatus estado;

    private String currentNodeId;
    private String currentNodeName;

    private RiskSeverity severidad;
    private String descripcionRiesgo;
    private String accionRecomendada;

    private long totalTasks;
    private long pendingTasks;
    private long completedTasks;
    private long delayedTasks;
}