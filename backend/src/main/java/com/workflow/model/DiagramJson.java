package com.workflow.model;

import lombok.Data;
import java.util.List;

@Data
public class DiagramJson {
    private List<WorkflowNode> nodes;
    private List<WorkflowConnection> connections;
}