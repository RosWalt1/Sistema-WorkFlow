package com.workflow.dto;

import com.workflow.model.ReportFormat;
import com.workflow.model.ReportIntent;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class DynamicReportResponse {

    private String title;
    private String originalQuery;
    private ReportIntent intent;
    private ReportFormat detectedFormat;
    private LocalDateTime generatedAt;
    private List<ReportColumnResponse> columns;
    private List<ReportRowResponse> rows;
}