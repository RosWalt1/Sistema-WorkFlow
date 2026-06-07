package com.workflow.repository;

import com.workflow.model.Task;
import com.workflow.model.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskRepository extends MongoRepository<Task, String> {

    List<Task> findByProcessInstanceId(String processInstanceId);

    List<Task> findByEstado(TaskStatus estado);

    List<Task> findByAssignedTo(String assignedTo);

    List<Task> findByAssignedToAndEstado(String assignedTo, TaskStatus estado);

    long countByEstado(TaskStatus estado);
}