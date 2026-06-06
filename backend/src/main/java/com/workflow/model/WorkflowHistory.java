package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowHistory {

    private LocalDateTime fecha;
    private String accion;
    private String detalle;
    private String nodeId;
}