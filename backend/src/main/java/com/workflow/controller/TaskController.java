package com.workflow.controller;

import com.workflow.dto.CompleteTaskRequest;
import com.workflow.model.Task;
import com.workflow.service.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> findAll() {
        return taskService.findAll();
    }

    @GetMapping("/pending")
    public List<Task> findPending() {
        return taskService.findPending();
    }

    @GetMapping("/process/{processInstanceId}")
    public List<Task> findByProcessInstanceId(@PathVariable String processInstanceId) {
        return taskService.findByProcessInstanceId(processInstanceId);
    }

    @GetMapping("/assigned/{assignedTo}")
    public List<Task> findByAssignedTo(@PathVariable String assignedTo) {
        return taskService.findByAssignedTo(assignedTo);
    }

    @GetMapping("/assigned/{assignedTo}/pending")
    public List<Task> findPendingByAssignedTo(@PathVariable String assignedTo) {
        return taskService.findPendingByAssignedTo(assignedTo);
    }

    @PostMapping("/{id}/complete")
    public Task completeTask(
            @PathVariable String id,
            @RequestBody CompleteTaskRequest request
    ) {
        return taskService.completeTask(id, request);
    }
}