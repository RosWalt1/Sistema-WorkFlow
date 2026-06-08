package com.workflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoutePredictionResponse {

    private String policyId;

    private List<String> rutaEsperada;

    private List<String> rutaReal;

    private List<String> desviaciones;

    private LocalDateTime fechaAnalisis;

    private String recomendacion;
}