package com.workflow.service;

import com.workflow.dto.AgentAnalyzeRequest;
import com.workflow.dto.AgentAnalyzeResponse;
import com.workflow.dto.AgentStartRequest;
import com.workflow.dto.StartProcessFromAgentRequest;
import com.workflow.model.AgentInteraction;
import com.workflow.model.BusinessPolicy;
import com.workflow.model.PolicyStatus;
import com.workflow.model.ProcessInstance;
import com.workflow.model.User;
import com.workflow.repository.AgentInteractionRepository;
import com.workflow.repository.BusinessPolicyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class IntelligentAgentService {

    private final BusinessPolicyRepository businessPolicyRepository;
    private final AgentInteractionRepository agentInteractionRepository;
    private final ProcessInstanceService processInstanceService;

    public IntelligentAgentService(
            BusinessPolicyRepository businessPolicyRepository,
            AgentInteractionRepository agentInteractionRepository,
            ProcessInstanceService processInstanceService
    ) {
        this.businessPolicyRepository = businessPolicyRepository;
        this.agentInteractionRepository = agentInteractionRepository;
        this.processInstanceService = processInstanceService;
    }

    public AgentAnalyzeResponse analyze(AgentAnalyzeRequest request) {
        AgentAnalyzeRequest safeRequest = Objects.requireNonNull(request, "La solicitud no puede ser null");

        String mensaje = normalizeText(safeRequest.getMensaje());

        BusinessPolicy bestPolicy = businessPolicyRepository.findAll()
                .stream()
                .filter(policy -> policy.getEstado() == PolicyStatus.ACTIVO)
                .max(Comparator.comparingInt(policy -> calculateScore(policy, mensaje)))
                .orElse(null);

        int score = bestPolicy == null ? 0 : calculateScore(bestPolicy, mensaje);

        AgentAnalyzeResponse response = new AgentAnalyzeResponse();
        response.setMensajeOriginal(safeRequest.getMensaje());
        response.setScore(score);
        response.setFound(bestPolicy != null && score > 0);

        if (bestPolicy != null && score > 0) {
            response.setIntention("TRAMITE_RECOMENDADO");
            response.setRecommendedPolicyId(bestPolicy.getId());
            response.setRecommendedPolicyName(bestPolicy.getNombre());
            response.setDescripcion(bestPolicy.getDescripcion());
            response.setRequiredDocuments(bestPolicy.getRequiredDocuments());
            response.setEstimatedDuration(bestPolicy.getEstimatedDuration());
            response.setRecommendedFor(bestPolicy.getRecommendedFor());
        } else {
            response.setIntention("NO_DETECTADO");
        }

        saveInteraction(
                safeRequest.getMensaje(),
                safeRequest.getClienteId(),
                response.getIntention(),
                response.getRecommendedPolicyId(),
                response.getRecommendedPolicyName(),
                response.getScore(),
                false,
                null
        );

        return response;
    }

    public ProcessInstance startRecommendedProcess(AgentStartRequest request) {
        AgentStartRequest safeRequest = Objects.requireNonNull(request, "La solicitud no puede ser null");

        StartProcessFromAgentRequest startRequest = new StartProcessFromAgentRequest();
        startRequest.setPolicyId(safeRequest.getPolicyId());
        startRequest.setClienteId(safeRequest.getClienteId());
        startRequest.setUserId(getCurrentUserId());
        startRequest.setInitialData(safeRequest.getInitialData());

        ProcessInstance processInstance = processInstanceService.startFromAgent(startRequest);

String safePolicyId = Objects.requireNonNull(
        safeRequest.getPolicyId(),
        "El policyId no puede ser null"
);

BusinessPolicy policy = businessPolicyRepository.findById(safePolicyId)
        .orElse(null);

        saveInteraction(
                safeRequest.getMensaje(),
                safeRequest.getClienteId(),
                "TRAMITE_INICIADO",
                safeRequest.getPolicyId(),
                policy != null ? policy.getNombre() : null,
                null,
                true,
                processInstance.getId()
        );

        return processInstance;
    }

    public List<AgentInteraction> history() {
        return agentInteractionRepository.findByUserIdOrderByFechaDesc(getCurrentUserId());
    }

    private int calculateScore(BusinessPolicy policy, String mensaje) {
        int score = 0;

        if (policy.getNombre() != null && mensaje.contains(normalizeText(policy.getNombre()))) {
            score += 3;
        }

        if (policy.getDescripcion() != null && mensaje.contains(normalizeText(policy.getDescripcion()))) {
            score += 2;
        }

        if (policy.getRecommendedFor() != null && mensaje.contains(normalizeText(policy.getRecommendedFor()))) {
            score += 2;
        }

        if (policy.getKeywords() != null) {
            for (String keyword : policy.getKeywords()) {
                if (keyword != null && !keyword.isBlank() && mensaje.contains(normalizeText(keyword))) {
                    score += 5;
                }
            }
        }

        return score;
    }

    private void saveInteraction(
            String mensaje,
            String clienteId,
            String intention,
            String policyId,
            String policyName,
            Integer score,
            boolean processStarted,
            String processInstanceId
    ) {
        AgentInteraction interaction = new AgentInteraction();
        interaction.setUserId(getCurrentUserId());
        interaction.setClienteId(clienteId);
        interaction.setMensaje(mensaje);
        interaction.setIntention(intention);
        interaction.setRecommendedPolicyId(policyId);
        interaction.setRecommendedPolicyName(policyName);
        interaction.setScore(score);
        interaction.setProcessStarted(processStarted);
        interaction.setProcessInstanceId(processInstanceId);
        interaction.setFecha(LocalDateTime.now());

        agentInteractionRepository.save(interaction);
    }

    private String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || authentication.getPrincipal() == null) {
            return "SYSTEM";
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof User user) {
            return user.getId();
        }

        return authentication.getName();
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }

        String normalized = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{M}", "").trim();
    }
}