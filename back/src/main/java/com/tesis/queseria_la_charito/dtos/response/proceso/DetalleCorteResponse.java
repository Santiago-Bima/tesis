package com.tesis.queseria_la_charito.dtos.response.proceso;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCorteResponse {
  @Description("id del detalle de corte")
  @JsonProperty("id")
  private Integer id;

  @Description("cantidad de queso")
  @JsonProperty("cantidad")
  private Integer cantidad;

  @Description("peso total")
  @JsonProperty("peso")
  private Double peso;

  @Description("corte")
  @JsonProperty("corte")
  private String corte;
}
