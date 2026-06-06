package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "process_instances")
public class ProcessInstance {

    @Id
    private String id;

    private String policyId;

    private ProcessStatus estado = ProcessStatus.EN_PROCESO;

    private LocalDateTime fechaInicio;

    private LocalDateTime fechaFin;

    private List<WorkflowHistory> historial = new ArrayList<>();
}