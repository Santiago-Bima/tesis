package com.tesis.queseria_la_charito.dtos.request.controlStock;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlStockRequest {
  @JsonProperty("fecha")
  private LocalDate fecha;

  @JsonProperty("cantidadEnterosObtenida")
  private Integer cantidadEnterosObtenida;

  @JsonProperty("cantidadMediosObtenida")
  private Integer cantidadMediosObtenida;

  @JsonProperty("cantidadCuartosObtenida")
  private Integer cantidadCuartosObtenida;

  @JsonProperty("cantidadesInsumos")
  private List<InsumoControlRequest> cantidadesInsumos;

  @JsonProperty("observaciones")
  private String observaciones;

  @JsonProperty("usuario")
  private String usuario;
}
