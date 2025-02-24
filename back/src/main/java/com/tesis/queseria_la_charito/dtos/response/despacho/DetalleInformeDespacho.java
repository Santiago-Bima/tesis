package com.tesis.queseria_la_charito.dtos.response.despacho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleInformeDespacho {
  @JsonProperty("destino")
  private DestinoResponse destino;

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
}
