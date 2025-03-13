package com.tesis.queseria_la_charito.dtos.response.stockControl;

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
public class BatchControlResponse {
  @Description("codigo de lote")
  @JsonProperty("codigo")
  private String id;

  @Description("item de lote")
  @JsonProperty("item")
  private String item;

  @Description("unidades")
  @JsonProperty("unidades")
  private Integer units;

  @JsonProperty("corte")
  private String cuts;
}
