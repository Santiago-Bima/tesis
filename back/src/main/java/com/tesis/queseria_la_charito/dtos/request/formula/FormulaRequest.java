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
    private String codigo;

    @JsonProperty("cantidad_leche")
    private Integer cantidadLeche;

    @JsonProperty("id_tipo_queso")
    private Long tipoQueso;

    @JsonProperty("detalles")
    private List<DetalleFormulaRequest> detallesFormulas;
}
