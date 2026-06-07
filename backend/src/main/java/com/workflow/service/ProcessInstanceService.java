package com.workflow.service;

import com.workflow.dto.StartProcessFromAgentRequest;
import com.workflow.model.*;
import com.workflow.repository.BusinessPolicyRepository;
import com.workflow.repository.ProcessInstanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ProcessInstanceService {

    private final ProcessInstanceRepository processInstanceRepository;
    private final BusinessPolicyRepository businessPolicyRepository;
    private final TaskService taskService;

    public ProcessInstanceService(
            ProcessInstanceRepository processInstanceRepository,
            BusinessPolicyRepository businessPolicyRepository,
            TaskService taskService
    ) {
        this.processInstanceRepository = processInstanceRepository;
        this.businessPolicyRepository = businessPolicyRepository;
        this.taskService = taskService;
    }

    public List<ProcessInstance> findAll() {
        return processInstanceRepository.findAll();
    }

    public ProcessInstance findById(String id) {
        String safeId = Objects.requireNonNull(id, "El id no puede ser null");

        return processInstanceRepository.findById(safeId)
                .orElseThrow(() -> new RuntimeException("Instancia de proceso no encontrada"));
    }

    public List<ProcessInstance> findByPolicyId(String policyId) {
        return processInstanceRepository.findByPolicyId(policyId);
    }

    public List<ProcessInstance> findByEstado(ProcessStatus estado) {
        return processInstanceRepository.findByEstado(estado);
    }

    public ProcessInstance startFromAgent(StartProcessFromAgentRequest request) {
        StartProcessFromAgentRequest safeRequest = Objects.requireNonNull(
                request,
                "La solicitud para iniciar proceso no puede ser null"
        );

        String safePolicyId = Objects.requireNonNull(
        safeRequest.getPolicyId(),
        "El policyId no puede ser null"
        );

        BusinessPolicy policy = businessPolicyRepository.findById(safePolicyId)
                .orElseThrow(() -> new RuntimeException("Política no encontrada"));

        WorkflowNode initialNode = obtenerNodoInicial(policy);

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setPolicyId(safePolicyId);
        processInstance.setClienteId(safeRequest.getClienteId());
        processInstance.setCurrentNodeId(initialNode.getId());
        processInstance.setCurrentNodeName(initialNode.getNombre());
        processInstance.setEstado(ProcessStatus.EN_PROCESO);
        processInstance.setFechaInicio(LocalDateTime.now());
        processInstance.setHistorial(new ArrayList<>());

        WorkflowHistory history = new WorkflowHistory();
        history.setFecha(LocalDateTime.now());
        history.setAccion("INICIO_DESDE_AGENTE");
        history.setDetalle("Proceso iniciado desde agente por usuario: " + safeRequest.getUserId());
        history.setNodeId(initialNode.getId());

        processInstance.getHistorial().add(history);

        ProcessInstance saved = processInstanceRepository.save(processInstance);
        ProcessInstance safeSaved = Objects.requireNonNull(saved, "No se pudo iniciar el proceso");

        taskService.createInitialTask(safeSaved, policy, initialNode);

        return safeSaved;
    }

    private WorkflowNode obtenerNodoInicial(BusinessPolicy policy) {
        if (policy.getDiagramaJson() == null || policy.getDiagramaJson().getNodes() == null) {
            throw new RuntimeException("La política no tiene diagrama configurado");
        }

        WorkflowNode inicio = policy.getDiagramaJson()
                .getNodes()
                .stream()
                .filter(node -> node.getTipo().equals("INICIO"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("La política no tiene nodo INICIO"));

        WorkflowConnection primeraConexion = policy.getDiagramaJson()
                .getConnections()
                .stream()
                .filter(connection -> connection.getOrigen().equals(inicio.getId()))
                .findFirst()
                .orElse(null);

        if (primeraConexion == null) {
            return inicio;
        }

        return policy.getDiagramaJson()
                .getNodes()
                .stream()
                .filter(node -> node.getId().equals(primeraConexion.getDestino()))
                .findFirst()
                .orElse(inicio);
    }
}