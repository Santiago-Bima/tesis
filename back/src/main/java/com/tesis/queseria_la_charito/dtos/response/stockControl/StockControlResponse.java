package com.tesis.queseria_la_charito.dtos.response.stockControl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.UserResponse;
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
public class StockControlResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("fecha")
  private LocalDate date;

  @JsonProperty("cantidadEnterosEsperada")
  private Integer wholeExpectedQuantity;

  @JsonProperty("cantidadEnterosObtenida")
  private Integer wholeQuantityObtained;

  @JsonProperty("CantidadMediosEsperada")
  private Integer halfExpectedQuantity;

  @JsonProperty("cantidadMediosObtenida")
  private Integer halfQuantityObtained;

  @JsonProperty("cantidadCuartosEsperada")
  private Integer quarterExpectedQuantity;

  @JsonProperty("cantidadCuartosObtenida")
  private Integer quarterQuantityObtained;

  @JsonProperty("cantidadesInsumosObtenidos")
  private List<SupplyControlResponse> suppliesQuantityObtained;

  @JsonProperty("cantidadesInsumosEsperados")
  private List<SupplyControlResponse> suppliesExpectedQuantity;

  @JsonProperty("observaciones")
  private String observations;

  @JsonProperty("usuario")
  private UserResponse responsible;

  @JsonProperty("nuevo")
  private boolean isNew;

}
