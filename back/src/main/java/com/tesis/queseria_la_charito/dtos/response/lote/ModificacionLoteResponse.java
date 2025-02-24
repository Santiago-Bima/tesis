package com.tesis.queseria_la_charito.dtos.response.lote;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tesis.queseria_la_charito.dtos.response.UsuarioResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ModificacionLoteResponse {
  @JsonProperty("id")
  private Long id;

  @JsonProperty("fecha")
  private LocalDate fecha;

  @JsonProperty("motivo")
  private String motivo;

  @JsonProperty("cantidadPrevia")
  private Integer cantidadPrevia;

  @JsonProperty("cantidadPosterior")
  private Integer cantidadPosterior;

  @JsonProperty("lote")
  private LoteResponse lote;

  @JsonProperty("usuario")
  private UsuarioResponse usuario;

  @JsonProperty("nuevo")
  private boolean nuevo;

}
