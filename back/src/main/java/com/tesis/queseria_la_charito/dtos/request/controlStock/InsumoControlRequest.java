package com.tesis.queseria_la_charito.dtos.request.controlStock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InsumoControlRequest {
  @JsonProperty("insumo")
  private String insumo;

  @JsonProperty("cantidad")
  private Integer cantidad;
}
