package com.workflow.controller;

import com.workflow.dto.CompleteTaskRequest;
import com.workflow.dto.StartProcessRequest;
import com.workflow.model.ProcessInstance;
import com.workflow.model.TaskInstance;
import com.workflow.service.WorkflowExecutionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/workflow-execution")
@CrossOrigin(origins = "*")
public class WorkflowExecutionController {

    private final WorkflowExecutionService workflowExecutionService;

    public WorkflowExecutionController(
            WorkflowExecutionService workflowExecutionService
    ) {
        this.workflowExecutionService = workflowExecutionService;
    }

    @PostMapping("/start")
    public ProcessInstance startProcess(
            @RequestBody StartProcessRequest request
    ) {
        return workflowExecutionService.startProcess(request);
    }

    @GetMapping("/tasks/pending")
    public List<TaskInstance> getPendingTasks() {
        return workflowExecutionService.getPendingTasks();
    }

    @GetMapping("/tasks/pending/{responsableId}")
    public List<TaskInstance> getPendingTasksByResponsable(
            @PathVariable String responsableId
    ) {
        return workflowExecutionService.getPendingTasksByResponsable(responsableId);
    }

    @PostMapping("/tasks/{taskId}/complete")
    public ProcessInstance completeTask(
            @PathVariable String taskId,
            @RequestBody CompleteTaskRequest request
    ) {
        return workflowExecutionService.completeTask(taskId, request);
    }

    @GetMapping("/process/{processId}")
    public ProcessInstance getProcess(
            @PathVariable String processId
    ) {
        return workflowExecutionService.getProcessById(processId);
    }
}