package com.tesis.queseria_la_charito.dtos.response.despacho;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.lote.LoteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleDespachoResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("lote")
  private LoteResponse lote;

  @JsonProperty("cantidadEnteros")
  private Integer cantidadEnteros;

  @JsonProperty("cantidadMedios")
  private Integer cantidadMedios;

  @JsonProperty("cantidadCuartos")
  private Integer cantidadCuartos;
}
