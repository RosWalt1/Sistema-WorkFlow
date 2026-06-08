package com.workflow.service;

import com.workflow.dto.BottleneckResponse;
import com.workflow.dto.ProcessRiskResponse;
import com.workflow.dto.RiskDashboardResponse;
import com.workflow.dto.RoutePredictionResponse;
import com.workflow.model.Anomaly;
import com.workflow.model.AnomalyStatus;
import com.workflow.model.BusinessPolicy;
import com.workflow.model.ProcessInstance;
import com.workflow.model.ProcessStatus;
import com.workflow.model.RiskSeverity;
import com.workflow.model.RoutePrediction;
import com.workflow.model.Task;
import com.workflow.model.TaskStatus;
//import com.workflow.model.WorkflowConnection;
import com.workflow.model.WorkflowHistory;
import com.workflow.model.WorkflowNode;
import com.workflow.repository.AnomalyRepository;
import com.workflow.repository.BusinessPolicyRepository;
import com.workflow.repository.ProcessInstanceRepository;
import com.workflow.repository.RoutePredictionRepository;
import com.workflow.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
public class RiskAnalysisService {

    private static final long DELAYED_TASK_MINUTES = 60;
    private static final long BOTTLENECK_PENDING_LIMIT = 3;

    private final AnomalyRepository anomalyRepository;
    private final RoutePredictionRepository routePredictionRepository;
    private final ProcessInstanceRepository processInstanceRepository;
    private final TaskRepository taskRepository;
    private final BusinessPolicyRepository businessPolicyRepository;

    public RiskAnalysisService(
            AnomalyRepository anomalyRepository,
            RoutePredictionRepository routePredictionRepository,
            ProcessInstanceRepository processInstanceRepository,
            TaskRepository taskRepository,
            BusinessPolicyRepository businessPolicyRepository
    ) {
        this.anomalyRepository = anomalyRepository;
        this.routePredictionRepository = routePredictionRepository;
        this.processInstanceRepository = processInstanceRepository;
        this.taskRepository = taskRepository;
        this.businessPolicyRepository = businessPolicyRepository;
    }

    public RiskDashboardResponse getDashboard() {
        long totalAnomalies = anomalyRepository.count();
        long openAnomalies = anomalyRepository.countByEstado(AnomalyStatus.ABIERTA);
        long resolvedAnomalies = anomalyRepository.countByEstado(AnomalyStatus.RESUELTA);

        long lowRisk = anomalyRepository.countBySeveridad(RiskSeverity.BAJA);
        long mediumRisk = anomalyRepository.countBySeveridad(RiskSeverity.MEDIA);
        long highRisk = anomalyRepository.countBySeveridad(RiskSeverity.ALTA);
        long criticalRisk = anomalyRepository.countBySeveridad(RiskSeverity.CRITICA);

        List<Task> pending = taskRepository.findByEstado(TaskStatus.PENDIENTE);
        long delayedTasks = pending.stream()
                .filter(this::isTaskDelayed)
                .count();

        long activeProcesses = processInstanceRepository.countByEstado(ProcessStatus.EN_PROCESO);

        return new RiskDashboardResponse(
                totalAnomalies,
                openAnomalies,
                resolvedAnomalies,
                lowRisk,
                mediumRisk,
                highRisk,
                criticalRisk,
                pending.size(),
                delayedTasks,
                activeProcesses
        );
    }

    public List<Anomaly> findAllAnomalies() {
        return anomalyRepository.findAll();
    }

    public List<Anomaly> findOpenAnomalies() {
        return anomalyRepository.findByEstado(AnomalyStatus.ABIERTA);
    }

    public List<Anomaly> findAnomaliesByProcess(String processId) {
        return anomalyRepository.findByProcessId(processId);
    }

    public ProcessRiskResponse analyzeProcessRisk(String processId) {
        ProcessInstance instance = findProcessOrThrow(processId);
        List<Task> tasks = taskRepository.findByProcessInstanceId(instance.getId());

        long totalTasks = tasks.size();
        long pendingTasks = tasks.stream()
                .filter(task -> task.getEstado() == TaskStatus.PENDIENTE)
                .count();

        long completedTasks = tasks.stream()
                .filter(task -> task.getEstado() == TaskStatus.COMPLETADA)
                .count();

        long delayedTasks = tasks.stream()
                .filter(this::isTaskDelayed)
                .count();

        RiskSeverity severity = calculateSeverity(pendingTasks, delayedTasks);

        String description = delayedTasks > 0
                ? "El proceso tiene tareas demoradas o riesgo de retraso."
                : "El proceso no presenta retrasos críticos actualmente.";

        String recommendedAction = delayedTasks > 0
                ? "Revisar responsables asignados y priorizar las tareas pendientes."
                : "Continuar monitoreando el avance del proceso.";

        return new ProcessRiskResponse(
                instance.getId(),
                instance.getPolicyId(),
                instance.getClienteId(),
                instance.getEstado(),
                instance.getCurrentNodeId(),
                instance.getCurrentNodeName(),
                severity,
                description,
                recommendedAction,
                totalTasks,
                pendingTasks,
                completedTasks,
                delayedTasks
        );
    }

    public List<BottleneckResponse> detectBottlenecks() {
        List<Task> pendingTasks = taskRepository.findByEstado(TaskStatus.PENDIENTE);
        List<String> assignedUsers = pendingTasks.stream()
                .map(Task::getAssignedTo)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<BottleneckResponse> responses = new ArrayList<>();

        for (String assignedTo : assignedUsers) {
            long count = pendingTasks.stream()
                    .filter(task -> assignedTo.equals(task.getAssignedTo()))
                    .count();

            if (count >= BOTTLENECK_PENDING_LIMIT) {
                RiskSeverity severity = count >= 6 ? RiskSeverity.CRITICA : RiskSeverity.ALTA;

                responses.add(new BottleneckResponse(
                        assignedTo,
                        count,
                        severity,
                        "Acumulación de tareas pendientes para el responsable o área: " + assignedTo,
                        "Redistribuir tareas o asignar apoyo operativo."
                ));
            }
        }

        return responses.stream()
                .sorted(Comparator.comparing(BottleneckResponse::getPendingTasks).reversed())
                .toList();
    }

    public void analyzeAfterTaskCompleted(Task completedTask) {
        if (completedTask == null || completedTask.getProcessInstanceId() == null) {
            return;
        }

        registerDelayedTaskIfNeeded(completedTask);
        registerProcessRiskIfNeeded(completedTask.getProcessInstanceId());
    }

    public RoutePredictionResponse analyzeRoute(String policyId) {
        BusinessPolicy policy = businessPolicyRepository.findById(
                Objects.requireNonNull(policyId, "El policyId no puede ser null")
        ).orElseThrow(() -> new RuntimeException("Política no encontrada"));

        List<String> expectedRoute = buildExpectedRoute(policy);
        List<String> realRoute = buildRealRoute(policyId);

        List<String> deviations = realRoute.stream()
                .filter(nodeName -> !expectedRoute.contains(nodeName))
                .toList();

        RoutePrediction prediction = new RoutePrediction();
        prediction.setPolicyId(policyId);
        prediction.setRutaEsperada(expectedRoute);
        prediction.setRutaReal(realRoute);
        prediction.setDesviaciones(deviations);
        prediction.setFechaAnalisis(LocalDateTime.now());

        routePredictionRepository.save(prediction);

        String recommendation = deviations.isEmpty()
                ? "La ruta real coincide con la ruta esperada."
                : "Revisar desviaciones detectadas y validar si el flujo requiere ajustes.";

        return new RoutePredictionResponse(
                policyId,
                expectedRoute,
                realRoute,
                deviations,
                prediction.getFechaAnalisis(),
                recommendation
        );
    }

    private void registerDelayedTaskIfNeeded(Task task) {
        if (!isCompletedTaskDelayed(task)) {
            return;
        }

        Anomaly anomaly = new Anomaly();
        anomaly.setProcessId(task.getProcessInstanceId());
        anomaly.setTaskId(task.getId());
        anomaly.setReglaId("TASK_DURATION_EXCEEDED");
        anomaly.setDescripcion("La tarea superó el tiempo esperado de ejecución: " + task.getNodeName());
        anomaly.setFecha(LocalDateTime.now());
        anomaly.setEstado(AnomalyStatus.ABIERTA);
        anomaly.setSeveridad(RiskSeverity.ALTA);
        anomaly.setAccionRecomendada("Revisar causa del retraso y ajustar carga de trabajo.");

        anomalyRepository.save(anomaly);
    }

    private void registerProcessRiskIfNeeded(String processId) {
        ProcessRiskResponse risk = analyzeProcessRisk(processId);

        if (risk.getSeveridad() == RiskSeverity.BAJA) {
            return;
        }

        Anomaly anomaly = new Anomaly();
        anomaly.setProcessId(processId);
        anomaly.setTaskId(null);
        anomaly.setReglaId("PROCESS_RISK_DETECTED");
        anomaly.setDescripcion(risk.getDescripcionRiesgo());
        anomaly.setFecha(LocalDateTime.now());
        anomaly.setEstado(AnomalyStatus.ABIERTA);
        anomaly.setSeveridad(risk.getSeveridad());
        anomaly.setAccionRecomendada(risk.getAccionRecomendada());

        anomalyRepository.save(anomaly);
    }

    private boolean isTaskDelayed(Task task) {
        if (task.getEstado() != TaskStatus.PENDIENTE || task.getFechaCreacion() == null) {
            return false;
        }

        long minutes = Duration.between(task.getFechaCreacion(), LocalDateTime.now()).toMinutes();
        return minutes > DELAYED_TASK_MINUTES;
    }

    private boolean isCompletedTaskDelayed(Task task) {
        if (task.getFechaCreacion() == null || task.getFechaFinalizacion() == null) {
            return false;
        }

        long minutes = Duration.between(task.getFechaCreacion(), task.getFechaFinalizacion()).toMinutes();
        return minutes > DELAYED_TASK_MINUTES;
    }

    private RiskSeverity calculateSeverity(long pendingTasks, long delayedTasks) {
        if (delayedTasks >= 3) {
            return RiskSeverity.CRITICA;
        }

        if (delayedTasks >= 1) {
            return RiskSeverity.ALTA;
        }

        if (pendingTasks >= 3) {
            return RiskSeverity.MEDIA;
        }

        return RiskSeverity.BAJA;
    }

    private ProcessInstance findProcessOrThrow(String processId) {
        String safeProcessId = Objects.requireNonNull(processId, "El processId no puede ser null");

        return processInstanceRepository.findById(safeProcessId)
                .orElseThrow(() -> new RuntimeException("Instancia de proceso no encontrada"));
    }

    private List<String> buildExpectedRoute(BusinessPolicy policy) {
        if (policy.getDiagramaJson() == null || policy.getDiagramaJson().getNodes() == null) {
            return List.of();
        }

        return policy.getDiagramaJson()
                .getNodes()
                .stream()
                .map(WorkflowNode::getNombre)
                .filter(Objects::nonNull)
                .toList();
    }

    private List<String> buildRealRoute(String policyId) {
        List<ProcessInstance> instances = processInstanceRepository.findByPolicyId(policyId);

        return instances.stream()
                .flatMap(instance -> instance.getHistorial().stream())
                .map(WorkflowHistory::getDetalle)
                .filter(Objects::nonNull)
                .toList();
    }
}