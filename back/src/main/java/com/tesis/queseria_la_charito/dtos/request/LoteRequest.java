package com.tesis.queseria_la_charito.dtos.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@NoArgsConstructor
@Data
@AllArgsConstructor
public class LoteRequest {
    @JsonProperty("fecha")
    private LocalDate fecha;

    @JsonProperty("unidades")
    private Integer unidades;

    @JsonProperty("motivos")
    private String motivos;

    @JsonProperty("usuario")
    private String usuario;
}
