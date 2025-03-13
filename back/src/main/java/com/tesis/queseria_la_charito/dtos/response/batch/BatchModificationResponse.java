package com.tesis.queseria_la_charito.dtos.response.batch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BatchModificationResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("fecha")
  private LocalDate date;

  @JsonProperty("motivo")
  private String reason;

  @JsonProperty("cantidadPrevia")
  private Integer previousQuantity;

  @JsonProperty("cantidadPosterior")
  private Integer subsequentQuantity ;

  @JsonProperty("lote")
  private BatchResponse batch;

  @JsonProperty("usuario")
  private UserResponse responsible;

  @JsonProperty("nuevo")
  private boolean isNew;

}
