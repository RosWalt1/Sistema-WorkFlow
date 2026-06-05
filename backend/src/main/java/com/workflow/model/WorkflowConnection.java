package com.workflow.model;

import lombok.Data;

@Data
public class WorkflowConnection {
    private String origen;
    private String destino;
    private String condicion;
}