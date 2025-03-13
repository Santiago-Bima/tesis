package com.tesis.queseria_la_charito.dtos.response.production;


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
public class ProductionReportResponse {
  @JsonProperty("cantidadElaboraciones")
  private Integer productionsQuantity;

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

  @JsonProperty("cantidadIncompletas")
  private Integer incompleteQuantity;

  @JsonProperty("insumosUtilizados")
  private List<SuppliesReportDetailElaboracion> lstUsedSupplies;
}
