package com.workflow.service;

import com.workflow.exception.ResourceNotFoundException;
import com.workflow.model.Department;
import com.workflow.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository repository;

    public List<Department> findAll() {
        return repository.findAll().stream().filter(item -> !item.isDeleted()).toList();
    }

    public Department findById(String id) {
    String safeId = id == null ? "" : id;

    return repository.findById(safeId)
            .filter(item -> !item.isDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("Department no encontrado"));
}

    public Department create(Department department) {
        department.setCreatedAt(LocalDateTime.now());
        department.setUpdatedAt(LocalDateTime.now());
        department.setDeleted(false);
        return repository.save(department);
    }

    public Department update(String id, Department request) {
        Department current = findById(id);
        current.setNombre(request.getNombre());
        current.setDescripcion(request.getDescripcion());
        current.setUpdatedAt(LocalDateTime.now());
        return repository.save(current);
    }

    public void delete(String id) {
        Department current = findById(id);
        current.setDeleted(true);
        current.setUpdatedAt(LocalDateTime.now());
        repository.save(current);
    }
}
