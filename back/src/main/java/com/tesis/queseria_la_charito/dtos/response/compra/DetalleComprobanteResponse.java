package com.tesis.queseria_la_charito.dtos.response.compra;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.lote.LoteResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleComprobanteResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("lote")
  private LoteResponse lote;

  @JsonProperty("proveedor")
  private ProveedorResponse proveedor;

  @JsonProperty("cantidad")
  private Integer cantidad;

  @JsonProperty("subtotal")
  private Integer subtotal;
}
