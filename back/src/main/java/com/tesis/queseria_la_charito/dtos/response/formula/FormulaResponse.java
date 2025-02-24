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
    private String codigo;

    @Description("cantidad de leche")
    @JsonProperty("cantidad_leche")
    private Integer cantidadLeche;

    @Description("Queso asociado")
    @JsonProperty("queso")
    private TipoQuesoResponse tipoQueso;

    @Description("Detalles")
    @JsonProperty("detalles")
    private List<DetalleFormulaResponse> detallesFormulas;
}
