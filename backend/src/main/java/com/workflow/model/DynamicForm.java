package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "dynamic_forms")
public class DynamicForm {

    @Id
    private String id;

    private String nombre;
    private String activityId;

    private List<DynamicField> campos = new ArrayList<>();
}