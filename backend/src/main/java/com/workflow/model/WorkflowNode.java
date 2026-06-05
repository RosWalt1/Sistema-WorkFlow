package com.workflow.model;

import lombok.Data;

@Data
public class WorkflowNode {
    private String id;
    private String tipo; // INICIO, ACTIVIDAD, DECISION, FIN
    private String nombre;
    private String calle;
    private Double posicionX;
    private Double posicionY;
    private String condiciones;
}