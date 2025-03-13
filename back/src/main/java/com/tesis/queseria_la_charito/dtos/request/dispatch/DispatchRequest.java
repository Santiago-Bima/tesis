package com.tesis.queseria_la_charito.dtos.request.dispatch;

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
public class DispatchRequest {
  @JsonProperty("fecha")
  private LocalDate date;

  @JsonProperty("idDestino")
  private Long destination;

  @JsonProperty("idVehiculo")
  private Long vehicle;

  @JsonProperty("queso")
  private String cheese;

  @JsonProperty("totalEnteros")
  private Integer totalWholes;

  @JsonProperty("totalMedios")
  private Integer totalHalfs;

  @JsonProperty("totalCuartos")
  private Integer totalQuarters;

  @JsonProperty("usuario")
  private String responsible;
}
