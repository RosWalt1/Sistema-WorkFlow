package com.workflow.repository;

import com.workflow.model.PolicyVersion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PolicyVersionRepository extends MongoRepository<PolicyVersion, String> {

    List<PolicyVersion> findByPolicyIdOrderByVersionNumberDesc(String policyId);

    long countByPolicyId(String policyId);
}