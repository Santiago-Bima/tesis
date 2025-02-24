package com.tesis.queseria_la_charito.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class ItemRequest {
    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("unidad_medida")
    private String unidadMedida;
}
