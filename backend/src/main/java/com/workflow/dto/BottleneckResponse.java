package com.workflow.dto;

import com.workflow.model.RiskSeverity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BottleneckResponse {

    private String assignedTo;

    private long pendingTasks;

    private RiskSeverity severidad;

    private String descripcion;

    private String accionRecomendada;
}