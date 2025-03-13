package com.tesis.queseria_la_charito.dtos.request.formula;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class FormulaRequest {
    @JsonProperty("codigo")
    private String code;

    @JsonProperty("cantidad_leche")
    private Integer milkQuantity;

    @JsonProperty("id_tipo_queso")
    private Long cheeseType;

    @JsonProperty("detalles")
    private List<FormulaDetailRequest> lstDetails;
}
