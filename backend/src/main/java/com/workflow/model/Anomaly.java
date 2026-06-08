package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "anomalies")
public class Anomaly {

    @Id
    private String id;

    private String processId;

    private String taskId;

    private String reglaId;

    private String descripcion;

    private LocalDateTime fecha;

    private AnomalyStatus estado = AnomalyStatus.ABIERTA;

    private RiskSeverity severidad;

    private String accionRecomendada;
}