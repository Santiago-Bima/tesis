package com.tesis.queseria_la_charito.dtos.request.procesosElaboracion;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCorteRequest {
  @JsonProperty("cantidad")
  private Integer cantidad;

  @JsonProperty("peso")
  private Double peso;

  @JsonProperty("corte")
  private String corte;
}
