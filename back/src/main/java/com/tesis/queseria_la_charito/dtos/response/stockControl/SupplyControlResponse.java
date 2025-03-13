package com.tesis.queseria_la_charito.dtos.response.stockControl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SupplyControlResponse {
  @JsonProperty("insumo")
  private String supply;

  @JsonProperty("cantidad")
  private Integer quantity;
}
