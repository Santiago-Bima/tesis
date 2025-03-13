package com.tesis.queseria_la_charito.dtos.request.formula;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class FormulaDetailRequest {
    @JsonProperty("insumo_id")
    private Long idSupply;

    @JsonProperty("cantidad")
    private Integer quantity;
}
