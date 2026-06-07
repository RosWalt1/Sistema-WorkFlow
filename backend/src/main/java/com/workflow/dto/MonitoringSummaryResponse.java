package com.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoringSummaryResponse {

    private long totalProcesses;
    private long processesInProgress;
    private long processesFinished;
    private long processesCancelled;

    private long totalTasks;
    private long pendingTasks;
    private long completedTasks;
    private long cancelledTasks;
}