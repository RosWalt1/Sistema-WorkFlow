package com.workflow.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "document_versions")
public class DocumentVersion {

    @Id
    private String id;

    private String documentFileId;
    private Integer versionNumber;
    private String originalFileName;
    private String objectKey;
    private String contentType;
    private Long size;
    private String createdBy;
    private LocalDateTime createdAt;
}