package com.workflow.service;

import com.workflow.exception.ResourceNotFoundException;
import com.workflow.model.Role;
import com.workflow.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    public List<Role> findAll() {
        return repository.findAll().stream().filter(item -> !item.isDeleted()).toList();
    }

    public Role findById(String id) {
    String safeId = id == null ? "" : id;

    return repository.findById(safeId)
            .filter(item -> !item.isDeleted())
            .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado"));
}

    public Role create(Role role) {
        role.setCreatedAt(LocalDateTime.now());
        role.setUpdatedAt(LocalDateTime.now());
        role.setDeleted(false);
        return repository.save(role);
    }

    public Role update(String id, Role request) {
        Role current = findById(id);
        current.setNombre(request.getNombre());
        current.setPermisos(request.getPermisos());
        current.setUpdatedAt(LocalDateTime.now());
        return repository.save(current);
    }

    public void delete(String id) {
        Role current = findById(id);
        current.setDeleted(true);
        current.setUpdatedAt(LocalDateTime.now());
        repository.save(current);
    }
}
