package com.tesis.queseria_la_charito.dtos.response.compra;

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
public class InformeCompraResponse {
  @JsonProperty("insumo")
  private String insumo;

  @JsonProperty("total")
  private Integer total;

  @JsonProperty("comprobantes")
  private List<DetalleComprobanteResponse> detalles;
}
