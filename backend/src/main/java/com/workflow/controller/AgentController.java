package com.workflow.controller;

import com.workflow.dto.AgentAnalyzeRequest;
import com.workflow.dto.AgentAnalyzeResponse;
import com.workflow.dto.AgentStartRequest;
import com.workflow.model.AgentInteraction;
import com.workflow.model.ProcessInstance;
import com.workflow.service.IntelligentAgentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/agent")
public class AgentController {

    private final IntelligentAgentService intelligentAgentService;

    public AgentController(IntelligentAgentService intelligentAgentService) {
        this.intelligentAgentService = intelligentAgentService;
    }

    @PostMapping("/analyze")
    public AgentAnalyzeResponse analyze(@RequestBody AgentAnalyzeRequest request) {
        return intelligentAgentService.analyze(request);
    }

    @PostMapping("/start")
    public ProcessInstance start(@RequestBody AgentStartRequest request) {
        return intelligentAgentService.startRecommendedProcess(request);
    }

    @GetMapping("/history")
    public List<AgentInteraction> history() {
        return intelligentAgentService.history();
    }
}