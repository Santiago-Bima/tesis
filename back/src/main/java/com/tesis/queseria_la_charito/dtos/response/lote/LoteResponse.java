package com.tesis.queseria_la_charito.dtos.response.lote;

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
public class LoteResponse {
    @Description("codigo de lote")
    @JsonProperty("codigo")
    private String id;

    @Description("item de lote")
    @JsonProperty("item")
    private ItemResponse item;

    @Description("QEstado de lote")
    @JsonProperty("estado")
    private String estado;

    @Description("unidades")
    @JsonProperty("unidades")
    private Integer unidades;
}
