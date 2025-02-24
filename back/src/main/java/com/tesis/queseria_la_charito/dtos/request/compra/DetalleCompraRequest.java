package com.tesis.queseria_la_charito.dtos.request.compra;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleCompraRequest {
  @JsonProperty("idProveedor")
  private Long idProveedor;

  @JsonProperty("cantidad")
  private Integer cantidad;

  @JsonProperty("subtotal")
  private Integer subtotal;
}
