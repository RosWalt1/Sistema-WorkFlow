package com.workflow.repository;

import com.workflow.model.DynamicForm;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface DynamicFormRepository extends MongoRepository<DynamicForm, String> {

    Optional<DynamicForm> findByActivityId(@NonNull String activityId);
}