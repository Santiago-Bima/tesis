package com.tesis.queseria_la_charito.dtos.response.despacho;

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
public class InformeDespachoResponse {
  @JsonProperty("cantidadDespachos")
  private Integer cantidadDespachos;

  @JsonProperty("totalUnidadesDespachadas")
  private Integer totalUnidadesDespachadas;

  @JsonProperty("cantidadTotalPategras")
  private Integer cantidadTotalPategras;

  @JsonProperty("cantidadTotalBarra")
  private Integer cantidadTotalBarra;

  @JsonProperty("cantidadTotalEnterosCremoso")
  private Integer cantidadTotalEnterosCremoso;

  @JsonProperty("cantidadTotalMediosCremoso")
  private Integer cantidadTotalMediosCremoso;

  @JsonProperty("cantidadTotalCuartosCremoso")
  private Integer cantidadTotalCuartosCremoso;

  @JsonProperty("detallesDespacho")
  List<DetalleInformeDespacho> detallesDespacho;
}
