package com.workflow.dto;

import lombok.Data;

@Data
public class StartProcessRequest {

    private String policyId;

    private String responsableId;
}