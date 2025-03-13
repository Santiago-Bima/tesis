package com.tesis.queseria_la_charito.dtos.request.production;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CutDetailRequest {
  @JsonProperty("cantidad")
  private Integer quantity;

  @JsonProperty("peso")
  private Double weight;

  @JsonProperty("corte")
  private String cut;
}
