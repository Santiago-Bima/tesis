package com.tesis.queseria_la_charito.dtos.response.proceso;

import com.fasterxml.jackson.annotation.JsonProperty;
import jdk.jfr.Description;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ControlCalidadResponse {
  @Description("id de control")
  @JsonProperty("id")
  private Long id;

  @Description("fecha")
  @JsonProperty("fecha")
  private LocalDate fecha;

  @Description("prueba de sabor")
  @JsonProperty("pruebaSabor")
  private String pruebaSabor;

  @Description("prueba de concistencia")
  @JsonProperty("pruebaConcistencia")
  private String pruebaConcistencia;

  @Description("prueba de aroma")
  @JsonProperty("pruebaAroma")
  private String pruebaAroma;

  @Description("observacion")
  @JsonProperty("observacion")
  private String observacion;
}
