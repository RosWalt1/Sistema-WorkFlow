package com.workflow.repository;

import com.workflow.model.TaskInstance;
import com.workflow.model.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TaskInstanceRepository extends MongoRepository<TaskInstance, String> {

    List<TaskInstance> findByProcessId(String processId);

    List<TaskInstance> findByEstado(TaskStatus estado);

    List<TaskInstance> findByResponsableIdAndEstado(String responsableId, TaskStatus estado);
}