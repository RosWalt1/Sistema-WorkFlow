package com.workflow.service;

import com.workflow.dto.CompleteTaskRequest;
import com.workflow.model.*;
import com.workflow.repository.BusinessPolicyRepository;
import com.workflow.repository.ProcessInstanceRepository;
import com.workflow.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProcessInstanceRepository processInstanceRepository;
    private final BusinessPolicyRepository businessPolicyRepository;

    public TaskService(
            TaskRepository taskRepository,
            ProcessInstanceRepository processInstanceRepository,
            BusinessPolicyRepository businessPolicyRepository) {
        this.taskRepository = taskRepository;
        this.processInstanceRepository = processInstanceRepository;
        this.businessPolicyRepository = businessPolicyRepository;
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findPending() {
        return taskRepository.findByEstado(TaskStatus.PENDIENTE);
    }

    public List<Task> findByProcessInstanceId(String processInstanceId) {
        return taskRepository.findByProcessInstanceId(processInstanceId);
    }

    public List<Task> findByAssignedTo(String assignedTo) {
        return taskRepository.findByAssignedTo(assignedTo);
    }

    public List<Task> findPendingByAssignedTo(String assignedTo) {
        return taskRepository.findByAssignedToAndEstado(assignedTo, TaskStatus.PENDIENTE);
    }

    public Task completeTask(String taskId, CompleteTaskRequest request) {
        String safeTaskId = Objects.requireNonNull(taskId, "El taskId no puede ser null");

        Task task = taskRepository.findById(safeTaskId)
                .orElseThrow(() -> new RuntimeException("Tarea no encontrada"));

        if (task.getEstado() != TaskStatus.PENDIENTE) {
            throw new RuntimeException("La tarea ya no está pendiente");
        }

        CompleteTaskRequest safeRequest = Objects.requireNonNull(
                request,
                "La solicitud para completar tarea no puede ser null");

        task.setEstado(TaskStatus.COMPLETADA);
        task.setFechaFinalizacion(LocalDateTime.now());

        String safeProcessInstanceId = Objects.requireNonNull(
                task.getProcessInstanceId(),
                "El processInstanceId no puede ser null");

        ProcessInstance instance = processInstanceRepository.findById(safeProcessInstanceId)
                .orElseThrow(() -> new RuntimeException("Instancia de proceso no encontrada"));

        String safePolicyId = Objects.requireNonNull(
                instance.getPolicyId(),
                "El policyId no puede ser null");

        BusinessPolicy policy = businessPolicyRepository.findById(safePolicyId)
                .orElseThrow(() -> new RuntimeException("Política no encontrada"));

        agregarHistorial(
                instance,
                "ACTIVIDAD_COMPLETADA",
                "Actividad completada por usuario: " + safeRequest.getUserId(),
                task.getNodeId());

        avanzarProceso(instance, policy, task);

        Task savedTask = taskRepository.save(task);
        return Objects.requireNonNull(savedTask, "No se pudo completar la tarea");
    }

    public Task createInitialTask(ProcessInstance instance, BusinessPolicy policy, WorkflowNode initialNode) {
        Task task = new Task();
        task.setProcessInstanceId(instance.getId());
        task.setPolicyId(policy.getId());
        task.setNodeId(initialNode.getId());
        task.setNodeName(initialNode.getNombre());
        task.setAssignedTo(initialNode.getCalle());
        task.setEstado(TaskStatus.PENDIENTE);
        task.setFechaCreacion(LocalDateTime.now());

        Task saved = taskRepository.save(task);
        return Objects.requireNonNull(saved, "No se pudo crear la tarea inicial");
    }

    private void avanzarProceso(ProcessInstance instance, BusinessPolicy policy, Task completedTask) {
        WorkflowConnection nextConnection = policy.getDiagramaJson()
                .getConnections()
                .stream()
                .filter(connection -> completedTask.getNodeId().equals(connection.getOrigen()))
                .findFirst()
                .orElse(null);

        if (nextConnection == null) {
            instance.setEstado(ProcessStatus.FINALIZADO);
            instance.setFechaFin(LocalDateTime.now());
            instance.setCurrentNodeId(null);
            instance.setCurrentNodeName(null);

            agregarHistorial(
                    instance,
                    "PROCESO_FINALIZADO",
                    "No existe siguiente conexión. Proceso finalizado.",
                    completedTask.getNodeId());

            processInstanceRepository.save(instance);
            return;
        }

        WorkflowNode nextNode = policy.getDiagramaJson()
                .getNodes()
                .stream()
                .filter(node -> node.getId().equals(nextConnection.getDestino()))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Nodo destino no encontrado"));

        instance.setCurrentNodeId(nextNode.getId());
        instance.setCurrentNodeName(nextNode.getNombre());

        if (nextNode.getTipo().equals("FIN")) {
            instance.setEstado(ProcessStatus.FINALIZADO);
            instance.setFechaFin(LocalDateTime.now());

            agregarHistorial(
                    instance,
                    "PROCESO_FINALIZADO",
                    "Proceso llegó al nodo FIN: " + nextNode.getNombre(),
                    nextNode.getId());

            processInstanceRepository.save(instance);
            return;
        }

        agregarHistorial(
                instance,
                "AVANCE_PROCESO",
                "Proceso avanzó al nodo: " + nextNode.getNombre(),
                nextNode.getId());

        ProcessInstance savedInstance = processInstanceRepository.save(instance);

        createInitialTask(savedInstance, policy, nextNode);
    }

    private void agregarHistorial(
            ProcessInstance instance,
            String accion,
            String detalle,
            String nodeId) {
        WorkflowHistory history = new WorkflowHistory();
        history.setFecha(LocalDateTime.now());
        history.setAccion(accion);
        history.setDetalle(detalle);
        history.setNodeId(nodeId);

        instance.getHistorial().add(history);
    }
}