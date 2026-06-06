package com.workflow.controller;

import com.workflow.dto.StartProcessFromAgentRequest;
import com.workflow.model.ProcessInstance;
import com.workflow.service.ProcessInstanceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/process")
public class ProcessInstanceController {

    private final ProcessInstanceService processInstanceService;

    public ProcessInstanceController(ProcessInstanceService processInstanceService) {
        this.processInstanceService = processInstanceService;
    }

    @PostMapping("/start-from-agent")
    public ProcessInstance startFromAgent(@RequestBody StartProcessFromAgentRequest request) {
        return processInstanceService.startFromAgent(request);
    }
}