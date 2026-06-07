package com.workflow.controller;

import com.workflow.dto.StartProcessFromAgentRequest;
import com.workflow.model.ProcessInstance;
import com.workflow.model.ProcessStatus;
import com.workflow.service.ProcessInstanceService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/process")
public class ProcessInstanceController {

    private final ProcessInstanceService processInstanceService;

    public ProcessInstanceController(ProcessInstanceService processInstanceService) {
        this.processInstanceService = processInstanceService;
    }

    @GetMapping
    public List<ProcessInstance> findAll() {
        return processInstanceService.findAll();
    }

    @GetMapping("/{id}")
    public ProcessInstance findById(@PathVariable String id) {
        return processInstanceService.findById(id);
    }

    @GetMapping("/policy/{policyId}")
    public List<ProcessInstance> findByPolicyId(@PathVariable String policyId) {
        return processInstanceService.findByPolicyId(policyId);
    }

    @GetMapping("/status/{estado}")
    public List<ProcessInstance> findByEstado(@PathVariable ProcessStatus estado) {
        return processInstanceService.findByEstado(estado);
    }

    @PostMapping("/start-from-agent")
    public ProcessInstance startFromAgent(@RequestBody StartProcessFromAgentRequest request) {
        return processInstanceService.startFromAgent(request);
    }
}