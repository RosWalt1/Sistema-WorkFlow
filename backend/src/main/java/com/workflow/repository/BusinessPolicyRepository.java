package com.workflow.repository;

import com.workflow.model.BusinessPolicy;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BusinessPolicyRepository extends MongoRepository<BusinessPolicy, String> {
}