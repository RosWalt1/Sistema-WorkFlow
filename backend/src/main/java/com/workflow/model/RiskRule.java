package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "risk_rules")
public class RiskRule {

    @Id
    private String id;

    private String nombre;

    private String condicion;

    private RiskSeverity severidad;

    private String accionRecomendada;
}