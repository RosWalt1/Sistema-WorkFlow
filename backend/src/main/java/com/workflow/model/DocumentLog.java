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
@Document(collection = "document_logs")
public class DocumentLog {

    @Id
    private String id;

    private String documentFileId;
    private String processInstanceId;
    private String taskId;
    private String userId;

    private String accion;
    private String descripcion;

    private LocalDateTime fecha;
}