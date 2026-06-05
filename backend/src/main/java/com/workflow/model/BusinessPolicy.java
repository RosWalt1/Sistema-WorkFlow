package com.workflow.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
//import java.util.List;

@Data
@Document(collection = "business_policies")
public class BusinessPolicy {

    @Id
    private String id;

    private String nombre;
    private String descripcion;
    private PolicyStatus estado = PolicyStatus.BORRADOR;
    private Integer version = 1;

    private DiagramJson diagramaJson;

    private String lockedBy;
    private LocalDateTime lockedAt;
}