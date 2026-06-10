package com.workflow.service;

import com.workflow.model.DocumentFile;
import com.workflow.model.DocumentLog;
import com.workflow.repository.DocumentFileRepository;
import com.workflow.repository.DocumentLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentFileRepository documentFileRepository;
    private final DocumentLogRepository documentLogRepository;
    private final S3Service s3Service;

    public DocumentService(
            DocumentFileRepository documentFileRepository,
            DocumentLogRepository documentLogRepository,
            S3Service s3Service
    ) {
        this.documentFileRepository = documentFileRepository;
        this.documentLogRepository = documentLogRepository;
        this.s3Service = s3Service;
    }

    public DocumentFile uploadDocument(
            MultipartFile file,
            String processInstanceId,
            String taskId,
            String policyId,
            String uploadedBy
    ) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo es obligatorio");
        }

        String originalFileName = file.getOriginalFilename();
        String safeOriginalName = originalFileName != null ? originalFileName : "archivo";
        String storedFileName = UUID.randomUUID() + "_" + safeOriginalName;
        String objectKey = "processes/" + processInstanceId + "/" + storedFileName;

        s3Service.uploadFile(file, objectKey);

        DocumentFile documentFile = new DocumentFile();
        documentFile.setProcessInstanceId(processInstanceId);
        documentFile.setTaskId(taskId);
        documentFile.setPolicyId(policyId);
        documentFile.setUploadedBy(uploadedBy);
        documentFile.setOriginalFileName(safeOriginalName);
        documentFile.setStoredFileName(storedFileName);
        documentFile.setContentType(file.getContentType());
        documentFile.setSize(file.getSize());
        documentFile.setBucketName(s3Service.getBucketName());
        documentFile.setObjectKey(objectKey);
        documentFile.setEstado("ACTIVO");
        documentFile.setFechaSubida(LocalDateTime.now());

        DocumentFile savedDocument = documentFileRepository.save(documentFile);

        registerLog(
                savedDocument,
                uploadedBy,
                "SUBIDA",
                "Documento subido al repositorio documental"
        );

        return savedDocument;
    }

    public InputStream downloadDocument(String documentId, String userId) {
        DocumentFile documentFile = getDocumentById(documentId);

        registerLog(
                documentFile,
                userId,
                "DESCARGA",
                "Documento descargado desde el repositorio documental"
        );

        return s3Service.downloadFile(documentFile.getObjectKey());
    }

public DocumentFile getDocumentById(String documentId) {
    if (documentId == null || documentId.isBlank()) {
        throw new RuntimeException("El ID del documento es obligatorio");
    }

    return documentFileRepository.findById(documentId)
            .orElseThrow(() -> new RuntimeException("Documento no encontrado"));
}

    public List<DocumentFile> getDocumentsByProcess(String processInstanceId) {
        return documentFileRepository.findByProcessInstanceId(processInstanceId);
    }

    public List<DocumentFile> getDocumentsByTask(String taskId) {
        return documentFileRepository.findByTaskId(taskId);
    }

    public List<DocumentLog> getDocumentHistory(String documentId) {
        return documentLogRepository.findByDocumentFileId(documentId);
    }

    private void registerLog(
            DocumentFile documentFile,
            String userId,
            String action,
            String description
    ) {
        DocumentLog log = new DocumentLog();
        log.setDocumentFileId(documentFile.getId());
        log.setProcessInstanceId(documentFile.getProcessInstanceId());
        log.setTaskId(documentFile.getTaskId());
        log.setUserId(userId);
        log.setAccion(action);
        log.setDescripcion(description);
        log.setFecha(LocalDateTime.now());

        documentLogRepository.save(log);
    }
}