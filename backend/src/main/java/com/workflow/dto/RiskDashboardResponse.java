package com.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskDashboardResponse {

    private long totalAnomalies;
    private long openAnomalies;
    private long resolvedAnomalies;

    private long lowRisk;
    private long mediumRisk;
    private long highRisk;
    private long criticalRisk;

    private long pendingTasks;
    private long delayedTasks;
    private long activeProcesses;
}