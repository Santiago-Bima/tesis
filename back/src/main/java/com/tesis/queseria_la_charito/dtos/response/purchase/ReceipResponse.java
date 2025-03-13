package com.tesis.queseria_la_charito.dtos.response.purchase;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReceipResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("fecha")
  private LocalDate date;

  @JsonProperty("total")
  private Integer total;

  @JsonProperty("detalles")
  private List<ReceipDetailResponse> lstDetails;
}
