package com.tesis.queseria_la_charito.dtos.response.dispatch;

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
public class DispatchReportResponse {
  @JsonProperty("cantidadDespachos")
  private Integer dispatchesQuantity;

  @JsonProperty("totalUnidadesDespachadas")
  private Integer totalUnitsDispatched;

  @JsonProperty("cantidadTotalPategras")
  private Integer totalPategrasDispatched;

  @JsonProperty("cantidadTotalBarra")
  private Integer totalBarraDispatched;

  @JsonProperty("cantidadTotalEnterosCremoso")
  private Integer totalWholeCremosoDispatched;

  @JsonProperty("cantidadTotalMediosCremoso")
  private Integer totalHalfCremosoDispatched;

  @JsonProperty("cantidadTotalCuartosCremoso")
  private Integer totalQuarterCremosoDispatched;

  @JsonProperty("detallesDespacho")
  List<ReportDetailDispatch> lstDetails;
}
