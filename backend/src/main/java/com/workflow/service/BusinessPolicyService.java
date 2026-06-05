package com.workflow.service;

import com.workflow.model.BusinessPolicy;
import com.workflow.model.PolicyStatus;
import com.workflow.repository.BusinessPolicyRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessPolicyService {

    private final BusinessPolicyRepository repository;

    public BusinessPolicyService(BusinessPolicyRepository repository) {
        this.repository = repository;
    }

    public List<BusinessPolicy> listar() {
        return repository.findAll();
    }

    public BusinessPolicy obtenerPorId(@NonNull String id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Política no encontrada"));
    }

    public BusinessPolicy crear(BusinessPolicy policy) {
        policy.setEstado(PolicyStatus.BORRADOR);
        policy.setVersion(1);
        return repository.save(policy);
    }

    public BusinessPolicy actualizar(@NonNull String id, BusinessPolicy datos) {
        BusinessPolicy policy = obtenerPorId(id);

        policy.setNombre(datos.getNombre());
        policy.setDescripcion(datos.getDescripcion());
        policy.setDiagramaJson(datos.getDiagramaJson());

        if (policy.getVersion() == null) {
            policy.setVersion(1);
        } else {
            policy.setVersion(policy.getVersion() + 1);
        }

        return repository.save(policy);
    }

    public void eliminar(@NonNull String id) {
        repository.deleteById(id);
    }

    public BusinessPolicy activar(@NonNull String id) {
        BusinessPolicy policy = obtenerPorId(id);
        policy.setEstado(PolicyStatus.ACTIVO);
        return repository.save(policy);
    }
}