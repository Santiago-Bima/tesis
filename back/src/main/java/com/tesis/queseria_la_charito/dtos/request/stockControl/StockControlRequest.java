package com.tesis.queseria_la_charito.dtos.request.stockControl;

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
public class StockControlRequest {
  @JsonProperty("fecha")
  private LocalDate date;

  @JsonProperty("cantidadEnterosObtenida")
  private Integer obtainedWholeQuantity;

  @JsonProperty("cantidadMediosObtenida")
  private Integer obtainedHalfQuantity;

  @JsonProperty("cantidadCuartosObtenida")
  private Integer obtainedQuarterQuantity;

  @JsonProperty("cantidadesInsumos")
  private List<InsumoControlRequest> suppliesQuantities;

  @JsonProperty("observaciones")
  private String observations;

  @JsonProperty("usuario")
  private String responsible;
}
