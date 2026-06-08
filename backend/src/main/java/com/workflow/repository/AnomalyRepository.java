package com.workflow.repository;

import com.workflow.model.Anomaly;
import com.workflow.model.AnomalyStatus;
import com.workflow.model.RiskSeverity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AnomalyRepository extends MongoRepository<Anomaly, String> {

    List<Anomaly> findByEstado(AnomalyStatus estado);

    List<Anomaly> findByProcessId(String processId);

    List<Anomaly> findByTaskId(String taskId);

    List<Anomaly> findBySeveridad(RiskSeverity severidad);

    long countByEstado(AnomalyStatus estado);

    long countBySeveridad(RiskSeverity severidad);
}