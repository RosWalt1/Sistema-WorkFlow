package com.workflow.controller;

import com.workflow.model.PolicyRequirement;
import com.workflow.service.PolicyRequirementService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/policy-requirements")
public class PolicyRequirementController {

    private final PolicyRequirementService policyRequirementService;

    public PolicyRequirementController(PolicyRequirementService policyRequirementService) {
        this.policyRequirementService = policyRequirementService;
    }

    @GetMapping
    public List<PolicyRequirement> findAll() {
        return policyRequirementService.findAll();
    }

    @GetMapping("/{id}")
    public PolicyRequirement findById(@PathVariable String id) {
        return policyRequirementService.findById(id);
    }

    @GetMapping("/policy/{policyId}")
    public PolicyRequirement findByPolicyId(@PathVariable String policyId) {
        return policyRequirementService.findByPolicyId(policyId);
    }

    @PostMapping
    public PolicyRequirement create(@RequestBody PolicyRequirement policyRequirement) {
        return policyRequirementService.create(policyRequirement);
    }

    @PutMapping("/{id}")
    public PolicyRequirement update(
            @PathVariable String id,
            @RequestBody PolicyRequirement policyRequirement
    ) {
        return policyRequirementService.update(id, policyRequirement);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        policyRequirementService.delete(id);
    }
}