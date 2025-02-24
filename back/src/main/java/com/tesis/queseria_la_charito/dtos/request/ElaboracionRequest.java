package com.tesis.queseria_la_charito.dtos.request;

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
public class ElaboracionRequest {
  @JsonProperty("fecha")
  private LocalDate fecha;

  @JsonProperty("cantidadLeche")
  private Integer cantidadLeche;

  @JsonProperty("idFormula")
  private String idFormula;

  @JsonProperty("tiempoSalado")
  private Integer tiempoSalado;

  @JsonProperty("usuario")
  private String usuario;
}
