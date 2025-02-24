package com.tesis.queseria_la_charito.dtos.request.despacho;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DespachoRequest {
  @JsonProperty("fecha")
  private LocalDate fecha;

  @JsonProperty("idDestino")
  private Long destino;

  @JsonProperty("idVehiculo")
  private Long vehiculo;

  @JsonProperty("queso")
  private String queso;

  @JsonProperty("totalEnteros")
  private Integer totalEnteros;

  @JsonProperty("totalMedios")
  private Integer totalMedios;

  @JsonProperty("totalCuartos")
  private Integer totalCuartos;

  @JsonProperty("usuario")
  private String usuario;
}
