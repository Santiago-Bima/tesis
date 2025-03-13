package com.tesis.queseria_la_charito.dtos.response.formula;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormulaResponse {
    @Description("codigo Formula")
    @JsonProperty("codigo")
    private String code;

    @Description("cantidad de leche")
    @JsonProperty("cantidad_leche")
    private Integer milkQuantity;

    @Description("Queso asociado")
    @JsonProperty("queso")
    private CheeseTypeResponse cheeseType;

    @Description("Detalles")
    @JsonProperty("detalles")
    private List<FormulaDetailResponse> lstDetails;
}
