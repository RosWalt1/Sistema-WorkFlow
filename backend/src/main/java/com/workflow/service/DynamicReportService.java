package com.workflow.service;

import com.workflow.dto.BottleneckResponse;
import com.workflow.dto.DynamicReportRequest;
import com.workflow.dto.DynamicReportResponse;
import com.workflow.dto.ReportColumnResponse;
import com.workflow.dto.ReportRowResponse;
import com.workflow.model.Anomaly;
import com.workflow.model.BusinessPolicy;
import com.workflow.model.ProcessInstance;
import com.workflow.model.ProcessStatus;
import com.workflow.model.ReportFormat;
import com.workflow.model.ReportIntent;
import com.workflow.model.RiskSeverity;
import com.workflow.model.Task;
import com.workflow.model.TaskStatus;
import com.workflow.repository.AnomalyRepository;
import com.workflow.repository.BusinessPolicyRepository;
import com.workflow.repository.ProcessInstanceRepository;
import com.workflow.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class DynamicReportService {

    private static final long DELAYED_TASK_MINUTES = 60;

    private final DynamicReportInterpreterService interpreterService;
    private final TaskRepository taskRepository;
    private final ProcessInstanceRepository processInstanceRepository;
    private final BusinessPolicyRepository businessPolicyRepository;
    private final AnomalyRepository anomalyRepository;
    private final RiskAnalysisService riskAnalysisService;

    public DynamicReportService(
            DynamicReportInterpreterService interpreterService,
            TaskRepository taskRepository,
            ProcessInstanceRepository processInstanceRepository,
            BusinessPolicyRepository businessPolicyRepository,
            AnomalyRepository anomalyRepository,
            RiskAnalysisService riskAnalysisService
    ) {
        this.interpreterService = interpreterService;
        this.taskRepository = taskRepository;
        this.processInstanceRepository = processInstanceRepository;
        this.businessPolicyRepository = businessPolicyRepository;
        this.anomalyRepository = anomalyRepository;
        this.riskAnalysisService = riskAnalysisService;
    }

    public DynamicReportResponse generateDynamicReport(DynamicReportRequest request) {
        String query = Objects.requireNonNull(request.getQuery(), "El comando no puede ser null");

        ReportIntent intent = interpreterService.detectIntent(query);
        ReportFormat format = interpreterService.detectFormat(query);

        return buildReport(intent, format, query);
    }

    public DynamicReportResponse buildReport(ReportIntent intent, ReportFormat format, String originalQuery) {
        return switch (intent) {
            case TASKS_PENDING -> buildTasksReport(
                    "Reporte de tareas pendientes",
                    originalQuery,
                    intent,
                    format,
                    taskRepository.findByEstado(TaskStatus.PENDIENTE)
            );

            case TASKS_COMPLETED -> buildTasksReport(
                    "Reporte de tareas completadas",
                    originalQuery,
                    intent,
                    format,
                    taskRepository.findByEstado(TaskStatus.COMPLETADA)
            );

            case PROCESSES_ACTIVE -> buildProcessesReport(
                    "Reporte de procesos activos",
                    originalQuery,
                    intent,
                    format,
                    processInstanceRepository.findByEstado(ProcessStatus.EN_PROCESO)
            );

            case PROCESSES_FINISHED -> buildProcessesReport(
                    "Reporte de procesos finalizados",
                    originalQuery,
                    intent,
                    format,
                    processInstanceRepository.findByEstado(ProcessStatus.FINALIZADO)
            );

            case PROCESSES_CANCELLED -> buildProcessesReport(
                    "Reporte de procesos cancelados",
                    originalQuery,
                    intent,
                    format,
                    processInstanceRepository.findByEstado(ProcessStatus.CANCELADO)
            );

            case ANOMALIES_OPEN -> buildAnomaliesReport(
                    "Reporte de anomalías abiertas",
                    originalQuery,
                    intent,
                    format,
                    anomalyRepository.findByEstado(com.workflow.model.AnomalyStatus.ABIERTA)
            );

            case ANOMALIES_CRITICAL -> buildAnomaliesReport(
                    "Reporte de anomalías críticas",
                    originalQuery,
                    intent,
                    format,
                    anomalyRepository.findBySeveridad(RiskSeverity.CRITICA)
            );

            case BOTTLENECKS -> buildBottlenecksReport(
                    "Reporte de cuellos de botella",
                    originalQuery,
                    intent,
                    format,
                    riskAnalysisService.detectBottlenecks()
            );

            case MOST_USED_POLICY -> buildMostUsedPolicyReport(
                    "Reporte de política más utilizada",
                    originalQuery,
                    intent,
                    format
            );

            case DELAYED_TASKS -> buildTasksReport(
                    "Reporte de tareas demoradas",
                    originalQuery,
                    intent,
                    format,
                    taskRepository.findByEstado(TaskStatus.PENDIENTE)
                            .stream()
                            .filter(this::isTaskDelayed)
                            .toList()
            );

            case UNKNOWN -> buildUnknownReport(originalQuery, format);
        };
    }

    private DynamicReportResponse buildTasksReport(
            String title,
            String originalQuery,
            ReportIntent intent,
            ReportFormat format,
            List<Task> tasks
    ) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("id", "ID"),
                new ReportColumnResponse("processInstanceId", "Proceso"),
                new ReportColumnResponse("nodeName", "Tarea"),
                new ReportColumnResponse("assignedTo", "Responsable"),
                new ReportColumnResponse("estado", "Estado"),
                new ReportColumnResponse("fechaCreacion", "Fecha creación"),
                new ReportColumnResponse("fechaFinalizacion", "Fecha finalización")
        );

        List<ReportRowResponse> rows = tasks.stream()
                .map(task -> row(Map.of(
                        "id", safe(task.getId()),
                        "processInstanceId", safe(task.getProcessInstanceId()),
                        "nodeName", safe(task.getNodeName()),
                        "assignedTo", safe(task.getAssignedTo()),
                        "estado", safe(task.getEstado()),
                        "fechaCreacion", safe(task.getFechaCreacion()),
                        "fechaFinalizacion", safe(task.getFechaFinalizacion())
                )))
                .toList();

        return response(title, originalQuery, intent, format, columns, rows);
    }

    private DynamicReportResponse buildProcessesReport(
            String title,
            String originalQuery,
            ReportIntent intent,
            ReportFormat format,
            List<ProcessInstance> processes
    ) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("id", "ID"),
                new ReportColumnResponse("policyId", "Política"),
                new ReportColumnResponse("clienteId", "Cliente"),
                new ReportColumnResponse("currentNodeName", "Nodo actual"),
                new ReportColumnResponse("estado", "Estado"),
                new ReportColumnResponse("fechaInicio", "Fecha inicio"),
                new ReportColumnResponse("fechaFin", "Fecha fin")
        );

        List<ReportRowResponse> rows = processes.stream()
                .map(process -> row(Map.of(
                        "id", safe(process.getId()),
                        "policyId", safe(process.getPolicyId()),
                        "clienteId", safe(process.getClienteId()),
                        "currentNodeName", safe(process.getCurrentNodeName()),
                        "estado", safe(process.getEstado()),
                        "fechaInicio", safe(process.getFechaInicio()),
                        "fechaFin", safe(process.getFechaFin())
                )))
                .toList();

        return response(title, originalQuery, intent, format, columns, rows);
    }

    private DynamicReportResponse buildAnomaliesReport(
            String title,
            String originalQuery,
            ReportIntent intent,
            ReportFormat format,
            List<Anomaly> anomalies
    ) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("id", "ID"),
                new ReportColumnResponse("processId", "Proceso"),
                new ReportColumnResponse("taskId", "Tarea"),
                new ReportColumnResponse("descripcion", "Descripción"),
                new ReportColumnResponse("estado", "Estado"),
                new ReportColumnResponse("severidad", "Severidad"),
                new ReportColumnResponse("accionRecomendada", "Acción recomendada"),
                new ReportColumnResponse("fecha", "Fecha")
        );

        List<ReportRowResponse> rows = anomalies.stream()
                .map(anomaly -> row(Map.of(
                        "id", safe(anomaly.getId()),
                        "processId", safe(anomaly.getProcessId()),
                        "taskId", safe(anomaly.getTaskId()),
                        "descripcion", safe(anomaly.getDescripcion()),
                        "estado", safe(anomaly.getEstado()),
                        "severidad", safe(anomaly.getSeveridad()),
                        "accionRecomendada", safe(anomaly.getAccionRecomendada()),
                        "fecha", safe(anomaly.getFecha())
                )))
                .toList();

        return response(title, originalQuery, intent, format, columns, rows);
    }

    private DynamicReportResponse buildBottlenecksReport(
            String title,
            String originalQuery,
            ReportIntent intent,
            ReportFormat format,
            List<BottleneckResponse> bottlenecks
    ) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("assignedTo", "Responsable"),
                new ReportColumnResponse("pendingTasks", "Tareas pendientes"),
                new ReportColumnResponse("severidad", "Severidad"),
                new ReportColumnResponse("descripcion", "Descripción"),
                new ReportColumnResponse("accionRecomendada", "Acción recomendada")
        );

        List<ReportRowResponse> rows = bottlenecks.stream()
                .map(item -> row(Map.of(
                        "assignedTo", safe(item.getAssignedTo()),
                        "pendingTasks", item.getPendingTasks(),
                        "severidad", safe(item.getSeveridad()),
                        "descripcion", safe(item.getDescripcion()),
                        "accionRecomendada", safe(item.getAccionRecomendada())
                )))
                .toList();

        return response(title, originalQuery, intent, format, columns, rows);
    }

    private DynamicReportResponse buildMostUsedPolicyReport(
            String title,
            String originalQuery,
            ReportIntent intent,
            ReportFormat format
    ) {
        Map<String, Long> usageByPolicy = processInstanceRepository.findAll()
                .stream()
                .filter(process -> process.getPolicyId() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        ProcessInstance::getPolicyId,
                        java.util.stream.Collectors.counting()
                ));

        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("policyId", "Política ID"),
                new ReportColumnResponse("policyName", "Nombre"),
                new ReportColumnResponse("totalProcesses", "Total de procesos")
        );

        List<ReportRowResponse> rows = usageByPolicy.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue(Comparator.reverseOrder()))
.map(entry -> {
    String policyId = Objects.requireNonNull(entry.getKey(), "El policyId no puede ser null");

    String policyName = businessPolicyRepository.findById(policyId)
            .map(BusinessPolicy::getNombre)
            .orElse("Política no encontrada");

    return row(Map.of(
            "policyId", policyId,
            "policyName", policyName,
            "totalProcesses", entry.getValue()
    ));
})
                .toList();

        return response(title, originalQuery, intent, format, columns, rows);
    }

    private DynamicReportResponse buildUnknownReport(String originalQuery, ReportFormat format) {
        List<ReportColumnResponse> columns = List.of(
                new ReportColumnResponse("mensaje", "Mensaje")
        );

        List<ReportRowResponse> rows = List.of(
                row(Map.of(
                        "mensaje", "No se pudo interpretar el comando. Intenta con: tareas pendientes, procesos activos, anomalías críticas o cuellos de botella."
                ))
        );

        return response(
                "Comando no reconocido",
                originalQuery,
                ReportIntent.UNKNOWN,
                format,
                columns,
                rows
        );
    }

    private boolean isTaskDelayed(Task task) {
        if (task.getEstado() != TaskStatus.PENDIENTE || task.getFechaCreacion() == null) {
            return false;
        }

        long minutes = Duration.between(task.getFechaCreacion(), LocalDateTime.now()).toMinutes();
        return minutes > DELAYED_TASK_MINUTES;
    }

    private DynamicReportResponse response(
            String title,
            String originalQuery,
            ReportIntent intent,
            ReportFormat format,
            List<ReportColumnResponse> columns,
            List<ReportRowResponse> rows
    ) {
        return new DynamicReportResponse(
                title,
                originalQuery,
                intent,
                format,
                LocalDateTime.now(),
                columns,
                rows
        );
    }

    private ReportRowResponse row(Map<String, Object> values) {
        return new ReportRowResponse(new LinkedHashMap<>(values));
    }

    private Object safe(Object value) {
        return value == null ? "" : value;
    }
}