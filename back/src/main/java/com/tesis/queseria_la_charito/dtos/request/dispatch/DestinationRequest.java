package com.tesis.queseria_la_charito.dtos.request.dispatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DestinationRequest {
  @JsonProperty("calle")
  private String street;

  @JsonProperty("numero")
  private Integer number;

  @JsonProperty("barrio")
  private String neighborhood;
}
