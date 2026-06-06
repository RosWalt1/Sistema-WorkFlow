package com.workflow.repository;

import com.workflow.model.PolicyRequirement;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PolicyRequirementRepository extends MongoRepository<PolicyRequirement, String> {

    Optional<PolicyRequirement> findByPolicyId(String policyId);
}