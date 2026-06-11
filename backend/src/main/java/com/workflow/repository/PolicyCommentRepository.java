package com.workflow.repository;

import com.workflow.model.PolicyComment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PolicyCommentRepository extends MongoRepository<PolicyComment, String> {

    List<PolicyComment> findByPolicyIdOrderByCreatedAtDesc(String policyId);
}