package com.tesis.queseria_la_charito.dtos.request.usuario;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationRequest {
  @JsonProperty(namespace = "username")
  private String username;

  @JsonProperty(namespace = "password")
  private String password;
}
