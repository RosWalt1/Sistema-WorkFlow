package com.workflow.repository;

import com.workflow.model.RiskRule;
import com.workflow.model.RiskSeverity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RiskRuleRepository extends MongoRepository<RiskRule, String> {

    List<RiskRule> findBySeveridad(RiskSeverity severidad);
}