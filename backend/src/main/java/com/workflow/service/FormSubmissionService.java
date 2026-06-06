package com.workflow.service;

import com.workflow.model.FormSubmission;
import com.workflow.repository.FormSubmissionRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FormSubmissionService {

    private final FormSubmissionRepository formSubmissionRepository;

    public FormSubmissionService(FormSubmissionRepository formSubmissionRepository) {
        this.formSubmissionRepository = formSubmissionRepository;
    }

    public List<FormSubmission> findAll() {
        return formSubmissionRepository.findAll();
    }

    public Optional<FormSubmission> findByActivityId(@NonNull String activityId) {
        return formSubmissionRepository.findByActivityId(activityId);
    }

    public FormSubmission save(@NonNull FormSubmission submission) {
        submission.setEstado("COMPLETADA");
        submission.setFechaEnvio(LocalDateTime.now());
        return formSubmissionRepository.save(submission);
    }
}