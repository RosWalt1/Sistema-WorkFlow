package com.workflow.controller;

import com.workflow.model.Role;
import com.workflow.response.ApiResponse;
import com.workflow.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Role>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Roles obtenidos correctamente", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Rol encontrado", service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Role>> create(@Valid @RequestBody Role role) {
        return ResponseEntity.ok(ApiResponse.ok("Rol creado correctamente", service.create(role)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Role>> update(@PathVariable String id, @Valid @RequestBody Role role) {
        return ResponseEntity.ok(ApiResponse.ok("Rol actualizado correctamente", service.update(id, role)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Rol eliminado logicamente", null));
    }
}
