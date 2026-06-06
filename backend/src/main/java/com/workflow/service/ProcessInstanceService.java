package com.workflow.service;

import com.workflow.dto.StartProcessFromAgentRequest;
import com.workflow.model.ProcessInstance;
import com.workflow.model.WorkflowHistory;
import com.workflow.repository.ProcessInstanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class ProcessInstanceService {

    private final ProcessInstanceRepository processInstanceRepository;

    public ProcessInstanceService(ProcessInstanceRepository processInstanceRepository) {
        this.processInstanceRepository = processInstanceRepository;
    }

    public ProcessInstance startFromAgent(StartProcessFromAgentRequest request) {
        StartProcessFromAgentRequest safeRequest = Objects.requireNonNull(
                request,
                "La solicitud para iniciar proceso no puede ser null"
        );

        ProcessInstance processInstance = new ProcessInstance();
        processInstance.setPolicyId(safeRequest.getPolicyId());
        processInstance.setClienteId(safeRequest.getClienteId());
        processInstance.setFechaInicio(LocalDateTime.now());

        WorkflowHistory history = new WorkflowHistory();
        history.setFecha(LocalDateTime.now());
        history.setAccion("INICIO_DESDE_AGENTE");
        history.setDetalle("Proceso iniciado desde agente por usuario: " + safeRequest.getUserId());
        history.setNodeId(null);

        processInstance.setHistorial(new ArrayList<>());
        processInstance.getHistorial().add(history);

        ProcessInstance saved = processInstanceRepository.save(processInstance);
        return Objects.requireNonNull(saved, "No se pudo iniciar el proceso");
    }
}