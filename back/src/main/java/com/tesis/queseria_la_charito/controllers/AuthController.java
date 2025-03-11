package com.tesis.queseria_la_charito.controllers;

import com.tesis.queseria_la_charito.dtos.request.LoginRequest;
import com.tesis.queseria_la_charito.dtos.request.RegisterRequest;
import com.tesis.queseria_la_charito.dtos.request.usuario.AuthenticationRequest;
import com.tesis.queseria_la_charito.dtos.request.usuario.UsuarioRequest;
import com.tesis.queseria_la_charito.dtos.response.AuthResponse;
import com.tesis.queseria_la_charito.dtos.response.usuario.UsuarioResponse;
import com.tesis.queseria_la_charito.services.AuthService;
import com.tesis.queseria_la_charito.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("autenticacion")
@RequiredArgsConstructor
public class AuthController {
  @Autowired
  private UsuarioService usuarioService;

  @Autowired
  private final AuthService authService;

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


//  Rutas de jwt
  @PostMapping("/login2")
  public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(authService.login(request));
  }

  @PostMapping("/refister")
  public  ResponseEntity<AuthResponse> register(@RequestBody RegisterRequest request) {
    return ResponseEntity.ok(authService.register(request));
  }

}
