package com.workflow.controller;

import com.workflow.model.DocumentFile;
import com.workflow.model.DocumentLog;
import com.workflow.service.DocumentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping("/upload")
    public ResponseEntity<DocumentFile> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("processInstanceId") String processInstanceId,
            @RequestParam(value = "taskId", required = false) String taskId,
            @RequestParam(value = "policyId", required = false) String policyId,
            @RequestParam("uploadedBy") String uploadedBy
    ) throws IOException {
        DocumentFile savedDocument = documentService.uploadDocument(
                file,
                processInstanceId,
                taskId,
                policyId,
                uploadedBy
        );

        return ResponseEntity.ok(savedDocument);
    }

    @GetMapping("/process/{processInstanceId}")
    public ResponseEntity<List<DocumentFile>> getDocumentsByProcess(
            @PathVariable String processInstanceId
    ) {
        return ResponseEntity.ok(documentService.getDocumentsByProcess(processInstanceId));
    }

    @GetMapping("/task/{taskId}")
    public ResponseEntity<List<DocumentFile>> getDocumentsByTask(
            @PathVariable String taskId
    ) {
        return ResponseEntity.ok(documentService.getDocumentsByTask(taskId));
    }

    @GetMapping("/{documentId}/history")
    public ResponseEntity<List<DocumentLog>> getDocumentHistory(
            @PathVariable String documentId
    ) {
        return ResponseEntity.ok(documentService.getDocumentHistory(documentId));
    }

    @GetMapping("/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable String documentId,
            @RequestParam("userId") String userId
    ) throws IOException {
        DocumentFile documentFile = documentService.getDocumentById(documentId);

        String contentType = documentFile.getContentType();
        if (contentType == null || contentType.isBlank()) {
            contentType = "application/octet-stream";
        }

        String originalFileName = documentFile.getOriginalFileName();
        if (originalFileName == null || originalFileName.isBlank()) {
            originalFileName = "documento";
        }

        try (InputStream inputStream = documentService.downloadDocument(documentId, userId)) {
            byte[] fileBytes = inputStream.readAllBytes();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + originalFileName + "\""
                    )
                    .body(fileBytes);
        }
    }
}