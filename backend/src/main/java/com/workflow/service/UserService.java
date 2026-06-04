package com.workflow.service;

import com.workflow.exception.ResourceNotFoundException;
import com.workflow.model.User;
import com.workflow.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public List<User> findAll() {
        return repository.findAll().stream()
                .filter(item -> !item.isDeleted())
                .toList();
    }

    public User findById(String id) {
        String safeId = id == null ? "" : id;

        return repository.findById(safeId)
                .filter(item -> !item.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User no encontrado"));
    }

    public User create(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setDeleted(false);
        return repository.save(user);
    }

    public User update(String id, User request) {
        User current = findById(id);

        current.setNombre(request.getNombre());
        current.setEmail(request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            current.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        current.setRol(request.getRol());
        current.setDepartamentoId(request.getDepartamentoId());
        current.setActivo(request.isActivo());
        current.setUpdatedAt(LocalDateTime.now());

        return repository.save(current);
    }

    public void delete(String id) {
        User current = findById(id);
        current.setDeleted(true);
        current.setUpdatedAt(LocalDateTime.now());
        repository.save(current);
    }

    public User activate(String id) {
        User current = findById(id);
        current.setActivo(true);
        current.setUpdatedAt(LocalDateTime.now());
        return repository.save(current);
    }

    public User deactivate(String id) {
        User current = findById(id);
        current.setActivo(false);
        current.setUpdatedAt(LocalDateTime.now());
        return repository.save(current);
    }
}