package com.workflow.service;

import com.workflow.model.BusinessPolicy;
import com.workflow.model.PolicyComment;
import com.workflow.model.PolicyStatus;
import com.workflow.model.PolicyVersion;
import com.workflow.repository.BusinessPolicyRepository;
import com.workflow.repository.PolicyCommentRepository;
import com.workflow.repository.PolicyVersionRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BusinessPolicyService {

    private static final long LOCK_TIMEOUT_MINUTES = 30;

    private final BusinessPolicyRepository repository;
    private final PolicyVersionRepository policyVersionRepository;
    private final PolicyCommentRepository policyCommentRepository;

    public BusinessPolicyService(
            BusinessPolicyRepository repository,
            PolicyVersionRepository policyVersionRepository,
            PolicyCommentRepository policyCommentRepository
    ) {
        this.repository = repository;
        this.policyVersionRepository = policyVersionRepository;
        this.policyCommentRepository = policyCommentRepository;
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
        policy.setBpmnXml(datos.getBpmnXml());
        policy.setKeywords(datos.getKeywords());
        policy.setRequiredDocuments(datos.getRequiredDocuments());
        policy.setEstimatedDuration(datos.getEstimatedDuration());
        policy.setRecommendedFor(datos.getRecommendedFor());

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

    public BusinessPolicy bloquear(@NonNull String id, @NonNull String userId) {
        if (userId.isBlank()) {
            throw new RuntimeException("El usuario es obligatorio para bloquear el diagrama");
        }

        BusinessPolicy policy = obtenerPorId(id);

        if (estaBloqueadaPorOtroUsuario(policy, userId)) {
            throw new RuntimeException("El diagrama está bloqueado por otro usuario");
        }

        policy.setLockedBy(userId);
        policy.setLockedAt(LocalDateTime.now());

        return repository.save(policy);
    }

    public BusinessPolicy desbloquear(@NonNull String id, @NonNull String userId) {
        if (userId.isBlank()) {
            throw new RuntimeException("El usuario es obligatorio para desbloquear el diagrama");
        }

        BusinessPolicy policy = obtenerPorId(id);

        if (policy.getLockedBy() != null
                && !policy.getLockedBy().isBlank()
                && !policy.getLockedBy().equals(userId)) {
            throw new RuntimeException("Solo el usuario que bloqueó el diagrama puede desbloquearlo");
        }

        policy.setLockedBy(null);
        policy.setLockedAt(null);

        return repository.save(policy);
    }

    public PolicyVersion crearVersion(
            @NonNull String policyId,
            @NonNull String createdBy,
            String description
    ) {
        if (createdBy.isBlank()) {
            throw new RuntimeException("El usuario es obligatorio para crear una versión");
        }

        BusinessPolicy policy = obtenerPorId(policyId);

        PolicyVersion version = new PolicyVersion();
        version.setPolicyId(policy.getId());
        version.setVersionNumber((int) policyVersionRepository.countByPolicyId(policyId) + 1);
        version.setDiagramSnapshot(policy.getDiagramaJson());
        version.setCreatedBy(createdBy);
        version.setCreatedAt(LocalDateTime.now());
        version.setDescription(description == null ? "" : description);

        return policyVersionRepository.save(version);
    }

    public List<PolicyVersion> listarVersiones(@NonNull String policyId) {
        obtenerPorId(policyId);
        return policyVersionRepository.findByPolicyIdOrderByVersionNumberDesc(policyId);
    }

    public BusinessPolicy restaurarVersion(
            @NonNull String policyId,
            @NonNull String versionId
    ) {
        BusinessPolicy policy = obtenerPorId(policyId);

        PolicyVersion version = policyVersionRepository.findById(versionId)
                .orElseThrow(() -> new RuntimeException("Versión no encontrada"));

        if (!policyId.equals(version.getPolicyId())) {
            throw new RuntimeException("La versión no pertenece a esta política");
        }

        policy.setDiagramaJson(version.getDiagramSnapshot());

        if (policy.getVersion() == null) {
            policy.setVersion(1);
        } else {
            policy.setVersion(policy.getVersion() + 1);
        }

        return repository.save(policy);
    }

    public PolicyComment crearComentario(
            @NonNull String policyId,
            String nodeId,
            @NonNull String message,
            @NonNull String createdBy
    ) {
        if (message.isBlank()) {
            throw new RuntimeException("El comentario no puede estar vacío");
        }

        if (createdBy.isBlank()) {
            throw new RuntimeException("El usuario es obligatorio para comentar");
        }

        obtenerPorId(policyId);

        PolicyComment comment = new PolicyComment();
        comment.setPolicyId(policyId);
        comment.setNodeId(nodeId == null ? "" : nodeId);
        comment.setMessage(message);
        comment.setCreatedBy(createdBy);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setResolved(false);

        return policyCommentRepository.save(comment);
    }

    public List<PolicyComment> listarComentarios(@NonNull String policyId) {
        obtenerPorId(policyId);
        return policyCommentRepository.findByPolicyIdOrderByCreatedAtDesc(policyId);
    }

    public PolicyComment resolverComentario(
            @NonNull String policyId,
            @NonNull String commentId
    ) {
        obtenerPorId(policyId);

        PolicyComment comment = policyCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!policyId.equals(comment.getPolicyId())) {
            throw new RuntimeException("El comentario no pertenece a esta política");
        }

        comment.setResolved(true);
        return policyCommentRepository.save(comment);
    }

    public void eliminarComentario(
            @NonNull String policyId,
            @NonNull String commentId
    ) {
        obtenerPorId(policyId);

        PolicyComment comment = policyCommentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comentario no encontrado"));

        if (!policyId.equals(comment.getPolicyId())) {
            throw new RuntimeException("El comentario no pertenece a esta política");
        }

        policyCommentRepository.deleteById(commentId);
    }

    private boolean estaBloqueadaPorOtroUsuario(BusinessPolicy policy, String userId) {
        if (policy.getLockedBy() == null || policy.getLockedBy().isBlank()) {
            return false;
        }

        if (policy.getLockedBy().equals(userId)) {
            return false;
        }

        if (policy.getLockedAt() == null) {
            return true;
        }

        long minutes = Duration.between(policy.getLockedAt(), LocalDateTime.now()).toMinutes();

        return minutes < LOCK_TIMEOUT_MINUTES;
    }
}