package com.workflow.service;

import com.workflow.model.DynamicForm;
import com.workflow.repository.DynamicFormRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DynamicFormService {

    private final DynamicFormRepository dynamicFormRepository;

    public DynamicFormService(DynamicFormRepository dynamicFormRepository) {
        this.dynamicFormRepository = dynamicFormRepository;
    }

    public List<DynamicForm> findAll() {
        return dynamicFormRepository.findAll();
    }

    public Optional<DynamicForm> findById(@NonNull String id) {
        return dynamicFormRepository.findById(id);
    }

    public Optional<DynamicForm> findByActivityId(@NonNull String activityId) {
        return dynamicFormRepository.findByActivityId(activityId);
    }

    public DynamicForm save(@NonNull DynamicForm dynamicForm) {
        return dynamicFormRepository.save(dynamicForm);
    }

    public DynamicForm update(@NonNull String id, @NonNull DynamicForm dynamicForm) {
        DynamicForm existingForm = dynamicFormRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formulario dinámico no encontrado"));

        existingForm.setNombre(dynamicForm.getNombre());
        existingForm.setActivityId(dynamicForm.getActivityId());
        existingForm.setCampos(dynamicForm.getCampos());

        return dynamicFormRepository.save(existingForm);
    }

    public void deleteById(@NonNull String id) {
        dynamicFormRepository.deleteById(id);
    }
}