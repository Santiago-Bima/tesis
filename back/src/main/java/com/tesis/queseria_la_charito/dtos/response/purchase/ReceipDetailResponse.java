package com.tesis.queseria_la_charito.dtos.response.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.batch.BatchResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceipDetailResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("lote")
  private BatchResponse batch;

  @JsonProperty("proveedor")
  private ProviderResponse provider;

  @JsonProperty("cantidad")
  private Integer quantity;

  @JsonProperty("subtotal")
  private Integer subtotal;
}
