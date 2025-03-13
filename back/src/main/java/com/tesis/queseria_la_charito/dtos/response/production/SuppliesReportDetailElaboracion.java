package com.tesis.queseria_la_charito.dtos.response.production;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SuppliesReportDetailElaboracion {
  @JsonProperty("insumo")
  private ItemResponse supply;

  @JsonProperty("total")
  private Integer total;
}
