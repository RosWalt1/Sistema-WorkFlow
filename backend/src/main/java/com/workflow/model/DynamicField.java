package com.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicField {

    private String nombre;
    private boolean tipoTexto;
    private boolean tipoFecha;
    private boolean tipoCheckbox;
    private boolean adjuntoRequerido;
}