package com.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReportColumnResponse {

    private String key;
    private String label;
}