package com.workflow.service;

import com.workflow.dto.MonitoringSummaryResponse;
import com.workflow.dto.ProcessMonitoringDetailResponse;
import com.workflow.dto.ProcessTraceResponse;
import com.workflow.dto.TaskDurationResponse;
import com.workflow.dto.TaskMonitoringResponse;
import com.workflow.model.ProcessInstance;
import com.workflow.model.ProcessStatus;
import com.workflow.model.Task;
import com.workflow.model.TaskStatus;
import com.workflow.repository.ProcessInstanceRepository;
import com.workflow.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class MonitoringService {

    private final ProcessInstanceRepository processInstanceRepository;
    private final TaskRepository taskRepository;

    public MonitoringService(
            ProcessInstanceRepository processInstanceRepository,
            TaskRepository taskRepository
    ) {
        this.processInstanceRepository = processInstanceRepository;
        this.taskRepository = taskRepository;
    }

    public MonitoringSummaryResponse getSummary() {
        long totalProcesses = processInstanceRepository.count();
        long processesInProgress = processInstanceRepository.countByEstado(ProcessStatus.EN_PROCESO);
        long processesFinished = processInstanceRepository.countByEstado(ProcessStatus.FINALIZADO);
        long processesCancelled = processInstanceRepository.countByEstado(ProcessStatus.CANCELADO);

        long totalTasks = taskRepository.count();
        long pendingTasks = taskRepository.countByEstado(TaskStatus.PENDIENTE);
        long completedTasks = taskRepository.countByEstado(TaskStatus.COMPLETADA);
        long cancelledTasks = taskRepository.countByEstado(TaskStatus.CANCELADA);

        return new MonitoringSummaryResponse(
                totalProcesses,
                processesInProgress,
                processesFinished,
                processesCancelled,
                totalTasks,
                pendingTasks,
                completedTasks,
                cancelledTasks
        );
    }

    public ProcessTraceResponse getProcessTrace(String processInstanceId) {
        ProcessInstance instance = findProcessInstanceOrThrow(processInstanceId);

        return new ProcessTraceResponse(
                instance.getId(),
                instance.getPolicyId(),
                instance.getClienteId(),
                instance.getCurrentNodeId(),
                instance.getCurrentNodeName(),
                instance.getEstado(),
                instance.getFechaInicio(),
                instance.getFechaFin(),
                instance.getHistorial()
        );
    }

    public ProcessMonitoringDetailResponse getProcessDetail(String processInstanceId) {
        ProcessInstance instance = findProcessInstanceOrThrow(processInstanceId);
        List<Task> tasks = taskRepository.findByProcessInstanceId(instance.getId());

        long totalTasks = tasks.size();
        long completedTasks = tasks.stream()
                .filter(task -> task.getEstado() == TaskStatus.COMPLETADA)
                .count();

        long pendingTasks = tasks.stream()
                .filter(task -> task.getEstado() == TaskStatus.PENDIENTE)
                .count();

        long cancelledTasks = tasks.stream()
                .filter(task -> task.getEstado() == TaskStatus.CANCELADA)
                .count();

        Double progressPercentage = calculateProgressPercentage(totalTasks, completedTasks);
        Long totalDurationMinutes = calculateProcessDurationMinutes(instance);

        List<TaskDurationResponse> taskDurations = tasks.stream()
                .map(this::mapToTaskDurationResponse)
                .toList();

        return new ProcessMonitoringDetailResponse(
                instance.getId(),
                instance.getPolicyId(),
                instance.getClienteId(),
                instance.getCurrentNodeId(),
                instance.getCurrentNodeName(),
                instance.getEstado(),
                instance.getFechaInicio(),
                instance.getFechaFin(),
                totalDurationMinutes,
                progressPercentage,
                totalTasks,
                completedTasks,
                pendingTasks,
                cancelledTasks,
                instance.getHistorial(),
                taskDurations
        );
    }

    public List<TaskDurationResponse> getTaskDurationsByProcess(String processInstanceId) {
        ProcessInstance instance = findProcessInstanceOrThrow(processInstanceId);

        return taskRepository.findByProcessInstanceId(instance.getId())
                .stream()
                .map(this::mapToTaskDurationResponse)
                .toList();
    }

    public List<TaskMonitoringResponse> getAllTasks() {
        return taskRepository.findAll()
                .stream()
                .map(this::mapToTaskMonitoringResponse)
                .toList();
    }

    public List<TaskMonitoringResponse> getPendingTasks() {
        return taskRepository.findByEstado(TaskStatus.PENDIENTE)
                .stream()
                .map(this::mapToTaskMonitoringResponse)
                .toList();
    }

    public List<TaskMonitoringResponse> getCompletedTasks() {
        return taskRepository.findByEstado(TaskStatus.COMPLETADA)
                .stream()
                .map(this::mapToTaskMonitoringResponse)
                .toList();
    }

    private ProcessInstance findProcessInstanceOrThrow(String processInstanceId) {
        String safeProcessInstanceId = Objects.requireNonNull(
                processInstanceId,
                "El processInstanceId no puede ser null"
        );

        return processInstanceRepository.findById(safeProcessInstanceId)
                .orElseThrow(() -> new RuntimeException("Instancia de proceso no encontrada"));
    }

    private Double calculateProgressPercentage(long totalTasks, long completedTasks) {
        if (totalTasks == 0) {
            return 0.0;
        }

        double progress = ((double) completedTasks / totalTasks) * 100;
        return Math.round(progress * 100.0) / 100.0;
    }

    private Long calculateProcessDurationMinutes(ProcessInstance instance) {
        if (instance.getFechaInicio() == null) {
            return null;
        }

        LocalDateTime endDate = instance.getFechaFin() != null
                ? instance.getFechaFin()
                : LocalDateTime.now();

        return Duration.between(instance.getFechaInicio(), endDate).toMinutes();
    }

    private TaskDurationResponse mapToTaskDurationResponse(Task task) {
        Long durationMinutes = null;

        if (task.getFechaCreacion() != null) {
            LocalDateTime endDate = task.getFechaFinalizacion() != null
                    ? task.getFechaFinalizacion()
                    : LocalDateTime.now();

            durationMinutes = Duration.between(task.getFechaCreacion(), endDate).toMinutes();
        }

        return new TaskDurationResponse(
                task.getId(),
                task.getNodeId(),
                task.getNodeName(),
                task.getAssignedTo(),
                task.getEstado(),
                task.getFechaCreacion(),
                task.getFechaFinalizacion(),
                durationMinutes
        );
    }

    private TaskMonitoringResponse mapToTaskMonitoringResponse(Task task) {
        return new TaskMonitoringResponse(
                task.getId(),
                task.getProcessInstanceId(),
                task.getPolicyId(),
                task.getNodeId(),
                task.getNodeName(),
                task.getAssignedTo(),
                task.getEstado(),
                task.getFechaCreacion(),
                task.getFechaFinalizacion()
        );
    }
}