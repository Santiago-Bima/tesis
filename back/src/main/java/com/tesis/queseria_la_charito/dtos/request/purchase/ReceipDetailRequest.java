package com.tesis.queseria_la_charito.dtos.request.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceipDetailRequest {
  @JsonProperty("idProveedor")
  private Long idProvider;

  @JsonProperty("cantidad")
  private Integer quantity;

  @JsonProperty("subtotal")
  private Integer subtotal;
}
