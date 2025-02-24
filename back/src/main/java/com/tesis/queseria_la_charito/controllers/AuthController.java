package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.dtos.request.usuario.AuthenticationRequest;
import com.tesis.queseria_la_charito.dtos.request.usuario.UsuarioRequest;
import com.tesis.queseria_la_charito.dtos.response.usuario.UsuarioResponse;
import com.tesis.queseria_la_charito.services.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("autenticacion")
public class AuthController {
  @Autowired
  private UsuarioService usuarioService;


  @PostMapping("/registro")
  public ResponseEntity<?> registrarUsuario(@RequestBody UsuarioRequest request) {
    return ResponseEntity.ok(usuarioService.registrarUsuario(request));
  }

  @PostMapping("/login")
  public ResponseEntity<?> autenticarUsuario(@RequestBody AuthenticationRequest loginRequest) {
    UsuarioResponse usuario = usuarioService.obtenerUsuarioPorNombreYContrasena(loginRequest.getUsername(), loginRequest.getPassword());
    if (!usuario.getUsername().isEmpty()) {
      return ResponseEntity.ok(usuario);
    }

    return ResponseEntity.badRequest().body("Los datos ingresados son incorrectos");
  }

  @PutMapping("/usuarios/{username}")
  public UsuarioResponse put(@PathVariable String username, @RequestParam String nombre) { return usuarioService.update(nombre, username); }

  @DeleteMapping("/usuarios/{username}")
  public UsuarioResponse delete(@PathVariable String username) { return usuarioService.delete(username); }

  @GetMapping("/usuarios")
  public List<UsuarioResponse> getAll() { return usuarioService.getAll(); }

  @GetMapping("/usuarios/{id}")
  public UsuarioResponse getById(@PathVariable Long id) { return usuarioService.getById(id); }
}
