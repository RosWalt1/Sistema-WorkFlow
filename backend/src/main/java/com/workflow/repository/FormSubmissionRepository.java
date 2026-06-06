package com.workflow.repository;

import com.workflow.model.FormSubmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

import java.util.Optional;

public interface FormSubmissionRepository extends MongoRepository<FormSubmission, String> {

    Optional<FormSubmission> findByActivityId(@NonNull String activityId);
}