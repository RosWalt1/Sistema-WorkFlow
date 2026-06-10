package com.workflow.repository;

import com.workflow.model.DocumentFile;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentFileRepository extends MongoRepository<DocumentFile, String> {

    List<DocumentFile> findByProcessInstanceId(String processInstanceId);

    List<DocumentFile> findByTaskId(String taskId);

    List<DocumentFile> findByUploadedBy(String uploadedBy);
}