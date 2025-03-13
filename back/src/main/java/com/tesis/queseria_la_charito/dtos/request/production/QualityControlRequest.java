package com.tesis.queseria_la_charito.dtos.request.production;

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
public class QualityControlRequest {
  @JsonProperty("fecha")
  private LocalDate date;

  @JsonProperty("pruebaSabor")
  private String tasteTest;

  @JsonProperty("pruebaConcistencia")
  private String consistencyTest;

  @JsonProperty("pruebaAroma")
  private String smellTest;

  @JsonProperty("observacion")
  private String observation;
}
