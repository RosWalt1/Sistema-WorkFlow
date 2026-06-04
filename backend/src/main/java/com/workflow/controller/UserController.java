package com.workflow.controller;

import com.workflow.model.User;
import com.workflow.response.ApiResponse;
import com.workflow.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<User>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok("Usuarios obtenidos correctamente", service.findAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> findById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario encontrado", service.findById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<User>> create(@Valid @RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario creado correctamente", service.create(user)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> update(@PathVariable String id, @Valid @RequestBody User user) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario actualizado correctamente", service.update(id, user)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("Usuario eliminado logicamente", null));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ApiResponse<User>> activate(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario activado", service.activate(id)));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse<User>> deactivate(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.ok("Usuario desactivado", service.deactivate(id)));
    }
}
