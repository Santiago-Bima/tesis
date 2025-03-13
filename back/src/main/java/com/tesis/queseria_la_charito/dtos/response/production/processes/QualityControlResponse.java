package com.tesis.queseria_la_charito.dtos.response.production.processes;

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
public class QualityControlResponse {
  @Description("id de control")
  @JsonProperty("id")
  private Long id;

  @Description("fecha")
  @JsonProperty("fecha")
  private LocalDate date;

  @Description("prueba de sabor")
  @JsonProperty("pruebaSabor")
  private String tasteTest;

  @Description("prueba de concistencia")
  @JsonProperty("pruebaConcistencia")
  private String consistencyTest;

  @Description("prueba de aroma")
  @JsonProperty("pruebaAroma")
  private String smellTest;

  @Description("observacion")
  @JsonProperty("observacion")
  private String observation;
}
