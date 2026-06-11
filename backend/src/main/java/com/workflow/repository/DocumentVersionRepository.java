package com.workflow.repository;

import com.workflow.model.DocumentVersion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DocumentVersionRepository extends MongoRepository<DocumentVersion, String> {

    List<DocumentVersion> findByDocumentFileIdOrderByVersionNumberDesc(String documentFileId);

    long countByDocumentFileId(String documentFileId);
}