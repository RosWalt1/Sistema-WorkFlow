package com.workflow.controller;

import com.workflow.model.FormSubmission;
import com.workflow.service.FormSubmissionService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/form-submissions")
public class FormSubmissionController {

    private final FormSubmissionService formSubmissionService;

    public FormSubmissionController(FormSubmissionService formSubmissionService) {
        this.formSubmissionService = formSubmissionService;
    }

    @GetMapping
    public List<FormSubmission> getAll() {
        return formSubmissionService.findAll();
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<FormSubmission> getByActivityId(@PathVariable @NonNull String activityId) {
        return formSubmissionService.findByActivityId(activityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public FormSubmission create(@RequestBody @NonNull FormSubmission submission) {
        return formSubmissionService.save(submission);
    }
}