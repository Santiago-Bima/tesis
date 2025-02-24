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
public class TipoQuesoResponse {
    @Description("Id Tipo Queso")
    @JsonProperty("id_tipo_queso")
    private Long idTipoQueso;

    @Description("Item asociado")
    @JsonProperty("item")
    private ItemResponse item;

    @Description("Dias de maduración")
    @JsonProperty("dias_maduracion")
    private Integer diasMaduracion;
}
