package com.tesis.queseria_la_charito.dtos.response.stockControl;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ExpectedQuantityResponse {
  @JsonProperty("cantidadEnterosEsperada")
  private Integer wholeExpectedQuantity;

  @JsonProperty("CantidadMediosEsperada")
  private Integer halfExpectedQuantity;

  @JsonProperty("cantidadCuartosEsperada")
  private Integer quarterExpectedQuantity;

  @JsonProperty("cantidadesInsumos")
  private List<SupplyControlResponse> suppliesQuantity;
}
