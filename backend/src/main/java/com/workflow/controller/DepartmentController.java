package com.workflow.controller;

import com.workflow.model.Department;
import com.workflow.response.ApiResponse;
import com.workflow.service.DepartmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Department>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Departamentos obtenidos correctamente", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Departamento encontrado", service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Department>> create(@Valid @RequestBody Department department) {
        return ResponseEntity.ok(ApiResponse.ok("Departamento creado correctamente", service.create(department)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> update(@PathVariable String id, @Valid @RequestBody Department department) {
        return ResponseEntity.ok(ApiResponse.ok("Departamento actualizado correctamente", service.update(id, department)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Departamento eliminado logicamente", null));
    }
}
