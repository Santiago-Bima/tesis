package com.tesis.queseria_la_charito.dtos.response.despacho;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.UsuarioResponse;
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
public class DespachoResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("fecha")
  private LocalDate fecha;

  @JsonProperty("queso")
  private String queso;

  @JsonProperty("cantidadTotal")
  private Integer cantidadTotal;

  @JsonProperty("destino")
  private DestinoResponse destino;

  @JsonProperty("vehiculo")
  private VehiculoResponse vehiculo;

  @JsonProperty("usuario")
  private UsuarioResponse usuario;

  @JsonProperty("estado")
  private String estado;

  @JsonProperty("detalles")
  private List<DetalleDespachoResponse> lstDetallesDespacho;
}
