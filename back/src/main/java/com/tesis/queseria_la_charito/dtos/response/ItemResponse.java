package com.tesis.queseria_la_charito.dtos.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponse {
    @Description("Item id")
    @JsonProperty("id")
    private Long idItem;

    @Description("Item nombre")
    @JsonProperty("nombre")
    private String nombreItem;

    @Description("Item unidad medida")
    @JsonProperty("unidad_medida")
    private String unidadMedidaItem;
}
