package com.workflow.controller;

import com.workflow.dto.BottleneckResponse;
import com.workflow.dto.ProcessRiskResponse;
import com.workflow.dto.RiskDashboardResponse;
import com.workflow.dto.RoutePredictionResponse;
import com.workflow.model.Anomaly;
import com.workflow.service.RiskAnalysisService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/risk")
public class RiskAnalysisController {

    private final RiskAnalysisService riskAnalysisService;

    public RiskAnalysisController(RiskAnalysisService riskAnalysisService) {
        this.riskAnalysisService = riskAnalysisService;
    }

    @GetMapping("/dashboard")
    public RiskDashboardResponse getDashboard() {
        return riskAnalysisService.getDashboard();
    }

    @GetMapping("/anomalies")
    public List<Anomaly> findAllAnomalies() {
        return riskAnalysisService.findAllAnomalies();
    }

    @GetMapping("/anomalies/open")
    public List<Anomaly> findOpenAnomalies() {
        return riskAnalysisService.findOpenAnomalies();
    }

    @GetMapping("/anomalies/process/{processId}")
    public List<Anomaly> findAnomaliesByProcess(@PathVariable String processId) {
        return riskAnalysisService.findAnomaliesByProcess(processId);
    }

    @GetMapping("/process/{processId}")
    public ProcessRiskResponse analyzeProcessRisk(@PathVariable String processId) {
        return riskAnalysisService.analyzeProcessRisk(processId);
    }

    @GetMapping("/bottlenecks")
    public List<BottleneckResponse> detectBottlenecks() {
        return riskAnalysisService.detectBottlenecks();
    }

    @PostMapping("/routes/analyze/{policyId}")
    public RoutePredictionResponse analyzeRoute(@PathVariable String policyId) {
        return riskAnalysisService.analyzeRoute(policyId);
    }
}