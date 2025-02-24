package com.tesis.queseria_la_charito.dtos.response.controlStock;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoteControlResponse {
  @Description("codigo de lote")
  @JsonProperty("codigo")
  private String id;

  @Description("item de lote")
  @JsonProperty("item")
  private String item;

  @Description("unidades")
  @JsonProperty("unidades")
  private Integer unidades;

  @JsonProperty("corte")
  private String corte;
}
