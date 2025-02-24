package com.tesis.queseria_la_charito.dtos.request.procesosElaboracion;

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
public class ControlCalidadRequest {
  @JsonProperty("fecha")
  private LocalDate fecha;

  @JsonProperty("pruebaSabor")
  private String pruebaSabor;

  @JsonProperty("pruebaConcistencia")
  private String pruebaConcistencia;

  @JsonProperty("pruebaAroma")
  private String pruebaAroma;

  @JsonProperty("observacion")
  private String observacion;
}
