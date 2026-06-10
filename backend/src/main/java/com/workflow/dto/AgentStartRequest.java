package com.workflow.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class AgentStartRequest {

    private String policyId;
    private String clienteId;
    private String mensaje;
    private Map<String, Object> initialData = new HashMap<>();
}