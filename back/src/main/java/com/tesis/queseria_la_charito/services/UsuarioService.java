package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.user.UserRequest;
import com.tesis.queseria_la_charito.dtos.response.usuario.UsuarioResponse;
import com.tesis.queseria_la_charito.entities.usuario.RolEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import com.tesis.queseria_la_charito.repositories.usuario.RolRepository;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private RolRepository rolRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Transactional
  public UsuarioResponse registrarUsuario(UserRequest usuario) {
    if (usuarioRepository.findByUsernameAndMostrar(usuario.getUsername(), true).isPresent()) {
      throw new EntityExistsException("ya existe un usuario con el mismo nombre");
    }

    UsuarioEntity usuarioEntity = modelMapper.map(usuario, UsuarioEntity.class);

    usuarioEntity.setMostrar(true);
    usuarioEntity.setPassword(usuario.getPassword());
    usuarioEntity.setIsDispatching(false);

    RolEntity rol = rolRepository.findByRol(usuario.getRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    usuarioEntity.setRol(rol);

    return modelMapper.map(usuarioRepository.save(usuarioEntity), UsuarioResponse.class);
  }

  public UsuarioResponse update(String nombre, String oldUsername) {
    if (usuarioRepository.findByUsernameAndMostrar(nombre, true).isPresent()) {
      throw new EntityExistsException("ya existe un usuario con el mismo nombre");
    }

    Optional<UsuarioEntity> optionalUsuarioEntity = usuarioRepository.findByUsernameAndMostrar(oldUsername, true);
    if (optionalUsuarioEntity.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    UsuarioEntity usuarioEntity = optionalUsuarioEntity.get();
    usuarioEntity.setUsername(nombre);

    return modelMapper.map(usuarioRepository.save(usuarioEntity), UsuarioResponse.class);
  }

  public UsuarioResponse getById(Long id) {
    Optional<UsuarioEntity> optionalUsuarioEntity = usuarioRepository.findById(id);
    if (optionalUsuarioEntity.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    return modelMapper.map(optionalUsuarioEntity, UsuarioResponse.class);
  }

  public List<UsuarioResponse> getAll() {
    List<UsuarioEntity> usuarioEntities = usuarioRepository.findByMostrarOrderByRolAsc(true);
    if (usuarioEntities.isEmpty()) {
      return new ArrayList<>();
    }

    List<UsuarioResponse> usuarioResponses = new ArrayList<>();
    for (UsuarioEntity usuarioEntity : usuarioEntities) {
      usuarioResponses.add(modelMapper.map(usuarioEntity, UsuarioResponse.class));
    }

    return usuarioResponses;
  }

  public UsuarioResponse obtenerUsuarioPorNombreYContrasena(String username, String password) {
    Optional<UsuarioEntity> usuarioResponseOptional = usuarioRepository.findByUsernameAndPasswordAndMostrar(username, password, true);
    if (usuarioResponseOptional.isEmpty()) {
      throw new EntityNotFoundException("Los datos son incorrectos");
    }
    return modelMapper.map(usuarioResponseOptional.get(), UsuarioResponse.class);
  }


  public UsuarioResponse delete(String username) {
    Optional<UsuarioEntity> optionalUsuarioEntity = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (optionalUsuarioEntity.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    UsuarioEntity usuarioEntity = optionalUsuarioEntity.get();
    usuarioEntity.setMostrar(false);

    return modelMapper.map(usuarioRepository.save(usuarioEntity), UsuarioResponse.class);
  }
}
