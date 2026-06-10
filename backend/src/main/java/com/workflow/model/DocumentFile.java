package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "document_files")
public class DocumentFile {

    @Id
    private String id;

    private String processInstanceId;
    private String taskId;
    private String policyId;
    private String uploadedBy;

    private String originalFileName;
    private String storedFileName;
    private String contentType;
    private Long size;

    private String bucketName;
    private String objectKey;

    private String estado = "ACTIVO";
    private LocalDateTime fechaSubida;
}