package com.workflow.service;

import com.workflow.dto.CompleteTaskRequest;
import com.workflow.dto.StartProcessRequest;
import com.workflow.model.BusinessPolicy;
import com.workflow.model.ProcessInstance;
import com.workflow.model.ProcessStatus;
import com.workflow.model.TaskInstance;
import com.workflow.model.TaskStatus;
import com.workflow.model.WorkflowConnection;
import com.workflow.model.WorkflowHistory;
import com.workflow.model.WorkflowNode;
import com.workflow.repository.BusinessPolicyRepository;
import com.workflow.repository.ProcessInstanceRepository;
import com.workflow.repository.TaskInstanceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WorkflowExecutionService {

    private final BusinessPolicyRepository businessPolicyRepository;
    private final ProcessInstanceRepository processInstanceRepository;
    private final TaskInstanceRepository taskInstanceRepository;

    public WorkflowExecutionService(
            BusinessPolicyRepository businessPolicyRepository,
            ProcessInstanceRepository processInstanceRepository,
            TaskInstanceRepository taskInstanceRepository) {
        this.businessPolicyRepository = businessPolicyRepository;
        this.processInstanceRepository = processInstanceRepository;
        this.taskInstanceRepository = taskInstanceRepository;
    }

    public ProcessInstance startProcess(StartProcessRequest request) {
        if (request == null) {
            throw new RuntimeException("La solicitud para iniciar el proceso es obligatoria");
        }

        String policyId = request.getPolicyId();
        String responsableId = request.getResponsableId();

        if (policyId == null || responsableId == null) {
            throw new RuntimeException("Datos incompletos para iniciar el proceso");
        }

        BusinessPolicy policy = businessPolicyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Política no encontrada"));

        WorkflowNode inicio = findNodeByTipo(policy, "INICIO");

        WorkflowNode primeraActividad = findNextNode(policy, inicio.getId(), null)
                .orElseThrow(() -> new RuntimeException("No existe una actividad después del nodo INICIO"));

        String primeraActividadId = primeraActividad.getId();

        if (primeraActividadId == null) {
            throw new RuntimeException("La primera actividad no tiene id");
        }

        ProcessInstance process = new ProcessInstance();
        process.setPolicyId(policy.getId());
        process.setEstado(ProcessStatus.EN_PROCESO);
        process.setFechaInicio(LocalDateTime.now());

        addHistory(
                process,
                "PROCESO_INICIADO",
                "Se inició el proceso desde la política: " + policy.getNombre(),
                inicio.getId());

        process = processInstanceRepository.save(process);

        String processId = process.getId();

        if (processId == null) {
            throw new RuntimeException("No se pudo obtener el id del proceso creado");
        }

        createTask(processId, primeraActividadId, responsableId);

        addHistory(
                process,
                "TAREA_CREADA",
                "Se creó la primera tarea: " + primeraActividad.getNombre(),
                primeraActividadId);

        return processInstanceRepository.save(process);
    }

    public List<TaskInstance> getPendingTasks() {
        return taskInstanceRepository.findByEstado(TaskStatus.PENDIENTE);
    }

    public List<TaskInstance> getPendingTasksByResponsable(String responsableId) {
        if (responsableId == null) {
            return Collections.emptyList();
        }

        return taskInstanceRepository.findByResponsableIdAndEstado(responsableId, TaskStatus.PENDIENTE);
    }

    public ProcessInstance getProcessById(String processId) {
        if (processId == null) {
            throw new RuntimeException("El id del proceso es obligatorio");
        }

        return processInstanceRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));
    }

    public ProcessInstance completeTask(String taskId, CompleteTaskRequest request) {
        if (taskId == null || request == null || request.getUserId() == null) {
            throw new RuntimeException("Datos incompletos para completar la tarea");
        }

        TaskInstance task = taskInstanceRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        String processId = task.getProcessId();

        if (processId == null) {
            throw new RuntimeException("La tarea no tiene processId");
        }

        ProcessInstance process = processInstanceRepository.findById(processId)
                .orElseThrow(() -> new RuntimeException("Proceso no encontrado"));

        String policyId = process.getPolicyId();

        if (policyId == null) {
            throw new RuntimeException("El proceso no tiene policyId");
        }

        BusinessPolicy policy = businessPolicyRepository.findById(policyId)
                .orElseThrow(() -> new RuntimeException("Política no encontrada"));

        task.setEstado(TaskStatus.COMPLETADA);
        task.setFechaCompletado(LocalDateTime.now());
        taskInstanceRepository.save(task);

        addHistory(
                process,
                "TAREA_COMPLETADA",
                "Se completó la tarea con activityId: " + task.getActivityId(),
                task.getActivityId());

        String activityId = task.getActivityId();

        if (activityId == null) {
            throw new RuntimeException("La tarea no tiene activityId");
        }

        advanceWorkflow(policy, process, activityId, request);

        return processInstanceRepository.save(process);
    }

    private void advanceWorkflow(
            BusinessPolicy policy,
            ProcessInstance process,
            String currentNodeId,
            CompleteTaskRequest request) {
        Optional<WorkflowNode> optionalNextNode = findNextNode(policy, currentNodeId, request);

        if (optionalNextNode.isEmpty()) {
            finishProcess(process, currentNodeId, "No se encontró siguiente nodo. El proceso fue finalizado.");
            return;
        }

        WorkflowNode nextNode = optionalNextNode.get();
        String tipo = nextNode.getTipo();

        if (tipo == null) {
            finishProcess(process, nextNode.getId(), "El siguiente nodo no tiene tipo. El proceso fue finalizado.");
            return;
        }

        if ("FIN".equalsIgnoreCase(tipo)) {
            finishProcess(process, nextNode.getId(), "El proceso llegó al nodo FIN.");
            return;
        }

        if ("ACTIVIDAD".equalsIgnoreCase(tipo)) {
            createNextTask(process, nextNode, request.getUserId(), "Se creó una nueva tarea: ");
            return;
        }

        if ("DECISION".equalsIgnoreCase(tipo)) {
            addHistory(
                    process,
                    "DECISION_EVALUADA",
                    "Se evaluó decisión: " + nextNode.getNombre(),
                    nextNode.getId());

            String decisionNodeId = nextNode.getId();

            if (decisionNodeId == null) {
                finishProcess(process, null, "La decisión no tiene id. El proceso fue finalizado.");
                return;
            }

            Optional<WorkflowNode> optionalNodeAfterDecision = findNextNode(policy, decisionNodeId, request);

            if (optionalNodeAfterDecision.isEmpty()) {
                finishProcess(process, decisionNodeId, "No se encontró ruta después de la decisión.");
                return;
            }

            WorkflowNode nodeAfterDecision = optionalNodeAfterDecision.get();
            String tipoAfterDecision = nodeAfterDecision.getTipo();

            if (tipoAfterDecision == null || "FIN".equalsIgnoreCase(tipoAfterDecision)) {
                finishProcess(process, nodeAfterDecision.getId(), "El proceso finalizó después de la decisión.");
                return;
            }

            if ("ACTIVIDAD".equalsIgnoreCase(tipoAfterDecision)) {
                createNextTask(process, nodeAfterDecision, request.getUserId(),
                        "Se creó una tarea después de la decisión: ");
            }
        }
    }

    private void createNextTask(
            ProcessInstance process,
            WorkflowNode node,
            String responsableId,
            String messagePrefix) {
        String processId = process.getId();
        String activityId = node.getId();

        if (processId == null || activityId == null || responsableId == null) {
            throw new RuntimeException("Datos incompletos para crear la siguiente tarea");
        }

        createTask(processId, activityId, responsableId);

        addHistory(
                process,
                "TAREA_CREADA",
                messagePrefix + node.getNombre(),
                activityId);
    }

    private void finishProcess(ProcessInstance process, String nodeId, String detail) {
        process.setEstado(ProcessStatus.FINALIZADO);
        process.setFechaFin(LocalDateTime.now());

        addHistory(
                process,
                "PROCESO_FINALIZADO",
                detail,
                nodeId);
    }

    private TaskInstance createTask(String processId, String activityId, String responsableId) {
        TaskInstance task = new TaskInstance();
        task.setProcessId(processId);
        task.setActivityId(activityId);
        task.setResponsableId(responsableId);
        task.setEstado(TaskStatus.PENDIENTE);
        task.setFechaAsignacion(LocalDateTime.now());

        return taskInstanceRepository.save(task);
    }

    private void addHistory(
            ProcessInstance process,
            String accion,
            String detalle,
            String nodeId) {
        WorkflowHistory history = new WorkflowHistory(
                LocalDateTime.now(),
                accion,
                detalle,
                nodeId);

        process.getHistorial().add(history);
    }

    private WorkflowNode findNodeByTipo(BusinessPolicy policy, String tipo) {
        List<WorkflowNode> nodes = policy.getDiagramaJson().getNodes();

        return nodes.stream()
                .filter(node -> {
                    String nodeTipo = node.getTipo();
                    return nodeTipo != null && nodeTipo.equalsIgnoreCase(tipo);
                })
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nodo tipo " + tipo + " no encontrado"));
    }

    private Optional<WorkflowNode> findNodeById(BusinessPolicy policy, String nodeId) {
        if (nodeId == null) {
            return Optional.empty();
        }

        List<WorkflowNode> nodes = policy.getDiagramaJson().getNodes();

        return nodes.stream()
                .filter(node -> nodeId.equals(node.getId()))
                .findFirst();
    }

    private Optional<WorkflowNode> findNextNode(
            BusinessPolicy policy,
            String currentNodeId,
            CompleteTaskRequest request) {
        if (currentNodeId == null) {
            return Optional.empty();
        }

        List<WorkflowConnection> connections = policy.getDiagramaJson().getConnections();

        Optional<WorkflowConnection> selectedConnection = connections.stream()
                .filter(connection -> currentNodeId.equals(connection.getOrigen()))
                .filter(connection -> matchCondition(connection, request))
                .findFirst();

        if (selectedConnection.isEmpty()) {
            return Optional.empty();
        }

        String destino = selectedConnection.get().getDestino();

        return findNodeById(policy, destino);
    }

    private boolean matchCondition(WorkflowConnection connection, CompleteTaskRequest request) {
        String condicion = connection.getCondicion();

        if (condicion == null || condicion.isBlank()) {
            return true;
        }

        if (request == null || request.getRespuestas() == null) {
            return false;
        }

        Map<String, Object> respuestas = request.getRespuestas();

        Object decision = respuestas.get("decision");

        if (decision == null) {
            decision = respuestas.get("Decision");
        }

        if (decision == null) {
            decision = respuestas.get("aprobado");
        }

        return decision != null && condicion.equalsIgnoreCase(String.valueOf(decision));
    }
}