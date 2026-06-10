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
@Document(collection = "agent_interactions")
public class AgentInteraction {

    @Id
    private String id;

    private String userId;
    private String clienteId;
    private String mensaje;
    private String intention;
    private String recommendedPolicyId;
    private String recommendedPolicyName;
    private Integer score;
    private boolean processStarted;
    private String processInstanceId;
    private LocalDateTime fecha;
}