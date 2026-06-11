package com.workflow.controller;

import com.workflow.model.BusinessPolicy;
import com.workflow.model.PolicyComment;
import com.workflow.model.PolicyVersion;
import com.workflow.service.BusinessPolicyService;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/business-policies")
@CrossOrigin(origins = "*")
public class BusinessPolicyController {

    private final BusinessPolicyService service;

    public BusinessPolicyController(BusinessPolicyService service) {
        this.service = service;
    }

    @GetMapping
    public List<BusinessPolicy> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public BusinessPolicy obtenerPorId(@PathVariable @NonNull String id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    public BusinessPolicy crear(@RequestBody BusinessPolicy policy) {
        return service.crear(policy);
    }

    @PutMapping("/{id}")
    public BusinessPolicy actualizar(
            @PathVariable @NonNull String id,
            @RequestBody BusinessPolicy policy
    ) {
        return service.actualizar(id, policy);
    }

    @DeleteMapping("/{id}")
    public void eliminar(@PathVariable @NonNull String id) {
        service.eliminar(id);
    }

    @PatchMapping("/{id}/activate")
    public BusinessPolicy activar(@PathVariable @NonNull String id) {
        return service.activar(id);
    }

    @PostMapping("/{id}/lock")
    public BusinessPolicy bloquear(
            @PathVariable @NonNull String id,
            @RequestBody Map<String, String> body
    ) {
        String userId = Objects.requireNonNull(body.getOrDefault("userId", ""));
        return service.bloquear(id, userId);
    }

    @PostMapping("/{id}/unlock")
    public BusinessPolicy desbloquear(
            @PathVariable @NonNull String id,
            @RequestBody Map<String, String> body
    ) {
        String userId = Objects.requireNonNull(body.getOrDefault("userId", ""));
        return service.desbloquear(id, userId);
    }

    @PostMapping("/{id}/versions")
    public PolicyVersion crearVersion(
            @PathVariable @NonNull String id,
            @RequestBody Map<String, String> body
    ) {
        String createdBy = Objects.requireNonNull(body.getOrDefault("createdBy", ""));
        String description = body.getOrDefault("description", "");
        return service.crearVersion(id, createdBy, description);
    }

    @GetMapping("/{id}/versions")
    public List<PolicyVersion> listarVersiones(@PathVariable @NonNull String id) {
        return service.listarVersiones(id);
    }

    @PostMapping("/{id}/versions/{versionId}/restore")
    public BusinessPolicy restaurarVersion(
            @PathVariable @NonNull String id,
            @PathVariable @NonNull String versionId
    ) {
        return service.restaurarVersion(id, versionId);
    }

    @PostMapping("/{id}/comments")
    public PolicyComment crearComentario(
            @PathVariable @NonNull String id,
            @RequestBody Map<String, String> body
    ) {
        String nodeId = body.getOrDefault("nodeId", "");
        String message = Objects.requireNonNull(body.getOrDefault("message", ""));
        String createdBy = Objects.requireNonNull(body.getOrDefault("createdBy", ""));
        return service.crearComentario(id, nodeId, message, createdBy);
    }

    @GetMapping("/{id}/comments")
    public List<PolicyComment> listarComentarios(@PathVariable @NonNull String id) {
        return service.listarComentarios(id);
    }

    @PatchMapping("/{id}/comments/{commentId}/resolve")
    public PolicyComment resolverComentario(
            @PathVariable @NonNull String id,
            @PathVariable @NonNull String commentId
    ) {
        return service.resolverComentario(id, commentId);
    }

    @DeleteMapping("/{id}/comments/{commentId}")
    public void eliminarComentario(
            @PathVariable @NonNull String id,
            @PathVariable @NonNull String commentId
    ) {
        service.eliminarComentario(id, commentId);
    }
}