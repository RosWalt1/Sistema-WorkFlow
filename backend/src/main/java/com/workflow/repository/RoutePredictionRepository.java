package com.workflow.repository;

import com.workflow.model.RoutePrediction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoutePredictionRepository extends MongoRepository<RoutePrediction, String> {

    List<RoutePrediction> findByPolicyId(String policyId);
}