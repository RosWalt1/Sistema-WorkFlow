package com.workflow.dto;

import com.workflow.model.ReportFormat;
import com.workflow.model.ReportIntent;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportExportRequest {

    @NotNull(message = "La intención del reporte es obligatoria")
    private ReportIntent intent;

    @NotNull(message = "El formato de exportación es obligatorio")
    private ReportFormat format;

    private String title;
}