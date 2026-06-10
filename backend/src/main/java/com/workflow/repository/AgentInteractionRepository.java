package com.workflow.repository;

import com.workflow.model.AgentInteraction;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AgentInteractionRepository extends MongoRepository<AgentInteraction, String> {

    List<AgentInteraction> findByUserIdOrderByFechaDesc(String userId);
}