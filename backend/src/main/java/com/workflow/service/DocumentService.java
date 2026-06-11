package com.workflow.service;

import com.workflow.model.DocumentFile;
import com.workflow.model.DocumentLog;
import com.workflow.model.DocumentVersion;
import com.workflow.repository.DocumentFileRepository;
import com.workflow.repository.DocumentLogRepository;
import com.workflow.repository.DocumentVersionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentService {

    private final DocumentFileRepository documentFileRepository;
    private final DocumentLogRepository documentLogRepository;
    private final DocumentVersionRepository documentVersionRepository;
    private final S3Service s3Service;

    @Value("${onlyoffice.document-server-url}")
    private String onlyOfficeDocumentServerUrl;

    @Value("${onlyoffice.backend-url}")
    private String backendUrl;

    public DocumentService(
            DocumentFileRepository documentFileRepository,
            DocumentLogRepository documentLogRepository,
            DocumentVersionRepository documentVersionRepository,
            S3Service s3Service
    ) {
        this.documentFileRepository = documentFileRepository;
        this.documentLogRepository = documentLogRepository;
        this.documentVersionRepository = documentVersionRepository;
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

        createDocumentVersion(savedDocument, uploadedBy);

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
System.out.println("DESCARGANDO DOCUMENTO ID: " + documentId);
System.out.println("OBJECT KEY: " + documentFile.getObjectKey());
System.out.println("BUCKET: " + s3Service.getBucketName());
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

    public List<DocumentVersion> getDocumentVersions(String documentId) {
        getDocumentById(documentId);
        return documentVersionRepository.findByDocumentFileIdOrderByVersionNumberDesc(documentId);
    }

    public Map<String, Object> getOnlyOfficeConfig(String documentId, String userId) {
        DocumentFile documentFile = getDocumentById(documentId);

        String fileName = documentFile.getOriginalFileName();
        String fileType = getFileExtension(fileName);

        Map<String, Object> document = new HashMap<>();
        document.put("fileType", fileType);
        document.put("key", documentFile.getId() + "-" + documentFile.getSize());
        document.put("title", fileName);
        document.put("url", backendUrl + "/api/documents/" + documentId + "/download?userId=" + userId);

        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("name", userId);

        Map<String, Object> editorConfig = new HashMap<>();
        editorConfig.put("mode", "view");
        editorConfig.put("lang", "es");
        editorConfig.put("callbackUrl", backendUrl + "/api/documents/onlyoffice/callback?documentId=" + documentId + "&userId=" + userId);
        editorConfig.put("user", user);

        Map<String, Object> config = new HashMap<>();
        config.put("documentType", getDocumentType(fileType));
        config.put("document", document);
        config.put("editorConfig", editorConfig);
        config.put("height", "100%");
        config.put("width", "100%");
        config.put("type", "desktop");
        config.put("documentServerUrl", onlyOfficeDocumentServerUrl);

        return config;
    }

    public void handleOnlyOfficeCallback(String documentId, String userId, Map<String, Object> body) {
        Object statusObject = body.get("status");

        if (!(statusObject instanceof Number statusNumber)) {
            return;
        }

        int status = statusNumber.intValue();

        if (status != 2 && status != 6) {
            return;
        }

        Object urlObject = body.get("url");

        if (!(urlObject instanceof String fileUrl) || fileUrl.isBlank()) {
            throw new RuntimeException("OnlyOffice no envió URL del documento actualizado");
        }

        try {
            DocumentFile documentFile = getDocumentById(documentId);

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fileUrl))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new RuntimeException("No se pudo descargar el documento actualizado desde OnlyOffice");
            }

            byte[] updatedBytes = response.body();

            String newObjectKey = "processes/"
                    + documentFile.getProcessInstanceId()
                    + "/versions/"
                    + UUID.randomUUID()
                    + "_"
                    + documentFile.getOriginalFileName();

            String contentType = documentFile.getContentType();
            if (contentType == null || contentType.isBlank()) {
                contentType = "application/octet-stream";
            }

            s3Service.uploadBytes(updatedBytes, newObjectKey, contentType);

            documentFile.setObjectKey(newObjectKey);
            documentFile.setSize((long) updatedBytes.length);
            DocumentFile savedDocument = documentFileRepository.save(documentFile);

            createDocumentVersion(savedDocument, userId);

            registerLog(
                    savedDocument,
                    userId,
                    "EDICION_ONLYOFFICE",
                    "Documento editado colaborativamente con OnlyOffice"
            );

        } catch (IOException e) {
            throw new RuntimeException("Error de lectura al procesar callback de OnlyOffice", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Proceso interrumpido al procesar callback de OnlyOffice", e);
        }
    }

    private void createDocumentVersion(DocumentFile documentFile, String createdBy) {
        DocumentVersion version = new DocumentVersion();
        version.setDocumentFileId(documentFile.getId());
        version.setVersionNumber((int) documentVersionRepository.countByDocumentFileId(documentFile.getId()) + 1);
        version.setOriginalFileName(documentFile.getOriginalFileName());
        version.setObjectKey(documentFile.getObjectKey());
        version.setContentType(documentFile.getContentType());
        version.setSize(documentFile.getSize());
        version.setCreatedBy(createdBy);
        version.setCreatedAt(LocalDateTime.now());

        documentVersionRepository.save(version);
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return "docx";
        }

        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private String getDocumentType(String fileType) {
        return switch (fileType) {
            case "doc", "docx", "odt", "rtf", "txt" -> "word";
            case "xls", "xlsx", "ods", "csv" -> "cell";
            case "ppt", "pptx", "odp" -> "slide";
            default -> "word";
        };
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