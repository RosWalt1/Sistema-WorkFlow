package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "route_predictions")
public class RoutePrediction {

    @Id
    private String id;

    private String policyId;

    private List<String> rutaEsperada = new ArrayList<>();

    private List<String> rutaReal = new ArrayList<>();

    private List<String> desviaciones = new ArrayList<>();

    private LocalDateTime fechaAnalisis;
}