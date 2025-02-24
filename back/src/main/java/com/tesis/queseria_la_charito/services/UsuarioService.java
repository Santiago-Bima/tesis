package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.usuario.UsuarioRequest;
import com.tesis.queseria_la_charito.dtos.response.usuario.UsuarioResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UsuarioService {
  UsuarioResponse registrarUsuario(UsuarioRequest usuario);

  UsuarioResponse update(String nombre, String oldUsername);

  UsuarioResponse getById(Long id);

  List<UsuarioResponse> getAll();

  UsuarioResponse obtenerUsuarioPorNombreYContrasena(String username, String password);


  UsuarioResponse delete(String username);
}
