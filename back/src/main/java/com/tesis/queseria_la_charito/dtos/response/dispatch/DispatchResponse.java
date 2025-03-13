package com.tesis.queseria_la_charito.dtos.response.dispatch;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.UserResponse;
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
public class DispatchResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("fecha")
  private LocalDate date;

  @JsonProperty("queso")
  private String cheese;

  @JsonProperty("cantidadTotal")
  private Integer totalQuantity;

  @JsonProperty("destino")
  private DestinationResponse destination;

  @JsonProperty("vehiculo")
  private VehicleResponse vehicle;

  @JsonProperty("usuario")
  private UserResponse responsible;

  @JsonProperty("estado")
  private String status;

  @JsonProperty("detalles")
  private List<DispatchDetailResponse> lstDetails;
}
