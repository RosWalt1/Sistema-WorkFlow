package com.workflow.repository;

import com.workflow.model.DocumentLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentLogRepository extends MongoRepository<DocumentLog, String> {

    List<DocumentLog> findByDocumentFileId(String documentFileId);

    List<DocumentLog> findByProcessInstanceId(String processInstanceId);
}