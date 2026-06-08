package com.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class ReportRowResponse {

    private Map<String, Object> values;
}