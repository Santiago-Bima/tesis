package com.tesis.queseria_la_charito.dtos.response.formula;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulaDetailResponse {
    @Description("Id de detalle")
    @JsonProperty("id_detalle")
    private Long id;

    @Description("Insumo")
    @JsonProperty("insumo")
    private ItemResponse supply;

    @Description("cantidad de insumo")
    @JsonProperty("cantidad")
    private Integer quantity;
}
