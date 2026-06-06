package com.workflow.controller;

import com.workflow.model.DynamicForm;
import com.workflow.service.DynamicFormService;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dynamic-forms")
public class DynamicFormController {

    private final DynamicFormService dynamicFormService;

    public DynamicFormController(DynamicFormService dynamicFormService) {
        this.dynamicFormService = dynamicFormService;
    }

    @GetMapping
    public List<DynamicForm> getAllForms() {
        return dynamicFormService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DynamicForm> getFormById(@PathVariable @NonNull String id) {
        return dynamicFormService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<DynamicForm> getFormByActivityId(@PathVariable @NonNull String activityId) {
        return dynamicFormService.findByActivityId(activityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public DynamicForm createForm(@RequestBody @NonNull DynamicForm dynamicForm) {
        return dynamicFormService.save(dynamicForm);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DynamicForm> updateForm(
            @PathVariable @NonNull String id,
            @RequestBody @NonNull DynamicForm dynamicForm
    ) {
        return ResponseEntity.ok(dynamicFormService.update(id, dynamicForm));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteForm(@PathVariable @NonNull String id) {
        dynamicFormService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}