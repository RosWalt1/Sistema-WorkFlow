package com.workflow.repository;

import com.workflow.model.ProcessInstance;
import com.workflow.model.ProcessStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProcessInstanceRepository extends MongoRepository<ProcessInstance, String> {

    List<ProcessInstance> findByPolicyId(String policyId);

    List<ProcessInstance> findByEstado(ProcessStatus estado);

    long countByEstado(ProcessStatus estado);
}