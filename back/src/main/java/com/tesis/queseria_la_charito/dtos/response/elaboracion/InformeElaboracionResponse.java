package com.tesis.queseria_la_charito.dtos.response.elaboracion;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformeElaboracionResponse {
  @JsonProperty("cantidadElaboraciones")
  private Integer cantidadElaboraciones;

  @JsonProperty("cantidadPategras")
  private Integer cantidadPategras;

  @JsonProperty("cantidadBarra")
  private Integer cantidadBarra;

  @JsonProperty("cantidadEnterosCremoso")
  private Integer cantidadEnterosCremoso;

  @JsonProperty("cantidadMediosCremoso")
  private Integer cantidadMediosCremoso;

  @JsonProperty("cantidadCuartosCremoso")
  private Integer cantidadCuartosCremoso;

  @JsonProperty("cantidadIncompletas")
  private Integer cantidadIncompletas;

  @JsonProperty("insumosUtilizados")
  private List<DetalleInsumoInformeElaboracion> insumosUtilizados;
}
