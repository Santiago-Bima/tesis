package com.tesis.queseria_la_charito.dtos.response.controlStock;

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
public class ControlStockResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("fecha")
  private LocalDate fecha;

  @JsonProperty("cantidadEnterosEsperada")
  private Integer cantidadEnterosEsperada;

  @JsonProperty("cantidadEnterosObtenida")
  private Integer cantidadEnterosObtenida;

  @JsonProperty("CantidadMediosEsperada")
  private Integer cantidadMediosEsperada;

  @JsonProperty("cantidadMediosObtenida")
  private Integer cantidadMediosObtenida;

  @JsonProperty("cantidadCuartosEsperada")
  private Integer cantidadCuartosEsperada;

  @JsonProperty("cantidadCuartosObtenida")
  private Integer cantidadCuartosObtenida;

  @JsonProperty("cantidadesInsumosObtenidos")
  private List<InsumoControlResponse> cantidadesInsumosObtenidos;

  @JsonProperty("cantidadesInsumosEsperados")
  private List<InsumoControlResponse> cantidadesInsumosEsperados;

  @JsonProperty("observaciones")
  private String observaciones;

  @JsonProperty("usuario")
  private UsuarioResponse usuario;

  @JsonProperty("nuevo")
  private boolean nuevo;

}
