package com.workflow.service;

import com.workflow.model.PolicyRequirement;
import com.workflow.repository.PolicyRequirementRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class PolicyRequirementService {

    private final PolicyRequirementRepository policyRequirementRepository;

    public PolicyRequirementService(PolicyRequirementRepository policyRequirementRepository) {
        this.policyRequirementRepository = policyRequirementRepository;
    }

    public List<PolicyRequirement> findAll() {
        return policyRequirementRepository.findAll();
    }

    public PolicyRequirement findById(String id) {
        String safeId = Objects.requireNonNull(id, "El id no puede ser null");

        return policyRequirementRepository.findById(safeId)
                .orElseThrow(() -> new RuntimeException("Requisito de política no encontrado"));
    }

    public PolicyRequirement findByPolicyId(String policyId) {
        String safePolicyId = Objects.requireNonNull(policyId, "El policyId no puede ser null");

        return policyRequirementRepository.findByPolicyId(safePolicyId)
                .orElseThrow(() -> new RuntimeException("Requisitos no encontrados para la política"));
    }

    public PolicyRequirement create(PolicyRequirement policyRequirement) {
        PolicyRequirement safePolicyRequirement = Objects.requireNonNull(
                policyRequirement,
                "El requisito de política no puede ser null");

        if (safePolicyRequirement.getInitialRequiredDocs() == null) {
            safePolicyRequirement.setInitialRequiredDocs(new ArrayList<>());
        }

        if (safePolicyRequirement.getInitialQuestions() == null) {
            safePolicyRequirement.setInitialQuestions(new ArrayList<>());
        }

        PolicyRequirement saved = policyRequirementRepository.save(safePolicyRequirement);
        return Objects.requireNonNull(saved, "No se pudo guardar el requisito de política");
    }

    public PolicyRequirement update(String id, PolicyRequirement request) {
        PolicyRequirement existing = findById(id);
        PolicyRequirement safeRequest = Objects.requireNonNull(
                request,
                "La solicitud de actualización no puede ser null");

        existing.setPolicyId(safeRequest.getPolicyId());

        existing.setInitialRequiredDocs(
                safeRequest.getInitialRequiredDocs() != null
                        ? safeRequest.getInitialRequiredDocs()
                        : new ArrayList<>());

        existing.setInitialQuestions(
                safeRequest.getInitialQuestions() != null
                        ? safeRequest.getInitialQuestions()
                        : new ArrayList<>());

        PolicyRequirement updated = policyRequirementRepository.save(existing);
        return Objects.requireNonNull(updated, "No se pudo actualizar el requisito de política");
    }

    public void delete(String id) {
        String safeId = Objects.requireNonNull(id, "El id no puede ser null");
        policyRequirementRepository.deleteById(safeId);
    }
}