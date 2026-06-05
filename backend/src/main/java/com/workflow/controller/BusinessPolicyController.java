package com.workflow.controller;

import com.workflow.model.BusinessPolicy;
import com.workflow.service.BusinessPolicyService;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}