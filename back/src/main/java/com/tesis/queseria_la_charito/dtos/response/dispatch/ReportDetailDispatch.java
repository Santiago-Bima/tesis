package com.tesis.queseria_la_charito.dtos.response.dispatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportDetailDispatch {
  @JsonProperty("destino")
  private DestinationResponse destination;

  @JsonProperty("cantidadPategras")
  private Integer pategrasQuantity;

  @JsonProperty("cantidadBarra")
  private Integer barraQuantity;

  @JsonProperty("cantidadEnterosCremoso")
  private Integer cremosoWholeQuantity;

  @JsonProperty("cantidadMediosCremoso")
  private Integer cremosoHalfQuantity;

  @JsonProperty("cantidadCuartosCremoso")
  private Integer cremosoQuarterQuantity;
}
