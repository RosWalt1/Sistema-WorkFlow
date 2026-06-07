package com.workflow.controller;

import com.workflow.dto.MonitoringSummaryResponse;
import com.workflow.dto.ProcessMonitoringDetailResponse;
import com.workflow.dto.ProcessTraceResponse;
import com.workflow.dto.TaskDurationResponse;
import com.workflow.dto.TaskMonitoringResponse;
import com.workflow.service.MonitoringService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/monitoring")
public class MonitoringController {

    private final MonitoringService monitoringService;

    public MonitoringController(MonitoringService monitoringService) {
        this.monitoringService = monitoringService;
    }

    @GetMapping("/summary")
    public MonitoringSummaryResponse getSummary() {
        return monitoringService.getSummary();
    }

    @GetMapping("/process/{processInstanceId}/trace")
    public ProcessTraceResponse getProcessTrace(@PathVariable String processInstanceId) {
        return monitoringService.getProcessTrace(processInstanceId);
    }

    @GetMapping("/process/{processInstanceId}/detail")
    public ProcessMonitoringDetailResponse getProcessDetail(@PathVariable String processInstanceId) {
        return monitoringService.getProcessDetail(processInstanceId);
    }

    @GetMapping("/process/{processInstanceId}/task-durations")
    public List<TaskDurationResponse> getTaskDurationsByProcess(@PathVariable String processInstanceId) {
        return monitoringService.getTaskDurationsByProcess(processInstanceId);
    }

    @GetMapping("/tasks")
    public List<TaskMonitoringResponse> getAllTasks() {
        return monitoringService.getAllTasks();
    }

    @GetMapping("/tasks/pending")
    public List<TaskMonitoringResponse> getPendingTasks() {
        return monitoringService.getPendingTasks();
    }

    @GetMapping("/tasks/completed")
    public List<TaskMonitoringResponse> getCompletedTasks() {
        return monitoringService.getCompletedTasks();
    }
}