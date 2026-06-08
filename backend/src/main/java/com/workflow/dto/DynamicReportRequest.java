package com.workflow.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DynamicReportRequest {

    @NotBlank(message = "El comando del reporte es obligatorio")
    private String query;
}