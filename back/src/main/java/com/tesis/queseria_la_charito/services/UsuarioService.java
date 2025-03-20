package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.user.UserRequest;
import com.tesis.queseria_la_charito.dtos.response.user.UserResponse;
import com.tesis.queseria_la_charito.entities.user.RoleEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import com.tesis.queseria_la_charito.repositories.user.RoleRepository;
import com.tesis.queseria_la_charito.repositories.user.UserRepository;
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
  private UserRepository usuarioRepository;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Transactional
  public UserResponse registrarUsuario(UserRequest usuario) {
    if (usuarioRepository.findByUsernameAndMostrar(usuario.getUsername(), true).isPresent()) {
      throw new EntityExistsException("ya existe un usuario con el mismo nombre");
    }

    UserEntity userEntity = modelMapper.map(usuario, UserEntity.class);

    userEntity.setMostrar(true);
    userEntity.setPassword(usuario.getPassword());
    userEntity.setIsDispatching(false);

    RoleEntity rol = roleRepository.findByRol(usuario.getRol()).orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    userEntity.setRol(rol);

    return modelMapper.map(usuarioRepository.save(userEntity), UserResponse.class);
  }

  public UserResponse update(String nombre, String oldUsername) {
    if (usuarioRepository.findByUsernameAndMostrar(nombre, true).isPresent()) {
      throw new EntityExistsException("ya existe un usuario con el mismo nombre");
    }

    Optional<UserEntity> optionalUsuarioEntity = usuarioRepository.findByUsernameAndMostrar(oldUsername, true);
    if (optionalUsuarioEntity.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    UserEntity userEntity = optionalUsuarioEntity.get();
    userEntity.setUsername(nombre);

    return modelMapper.map(usuarioRepository.save(userEntity), UserResponse.class);
  }

  public UserResponse getById(Long id) {
    Optional<UserEntity> optionalUsuarioEntity = usuarioRepository.findById(id);
    if (optionalUsuarioEntity.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    return modelMapper.map(optionalUsuarioEntity, UserResponse.class);
  }

  public List<UserResponse> getAll() {
    List<UserEntity> usuarioEntities = usuarioRepository.findByMostrarOrderByRolAsc(true);
    if (usuarioEntities.isEmpty()) {
      return new ArrayList<>();
    }

    List<UserResponse> usuarioResponses = new ArrayList<>();
    for (UserEntity userEntity : usuarioEntities) {
      usuarioResponses.add(modelMapper.map(userEntity, UserResponse.class));
    }

    return usuarioResponses;
  }

  public UserResponse obtenerUsuarioPorNombreYContrasena(String username, String password) {
    Optional<UserEntity> usuarioResponseOptional = usuarioRepository.findByUsernameAndPasswordAndMostrar(username, password, true);
    if (usuarioResponseOptional.isEmpty()) {
      throw new EntityNotFoundException("Los datos son incorrectos");
    }
    return modelMapper.map(usuarioResponseOptional.get(), UserResponse.class);
  }


  public UserResponse delete(String username) {
    Optional<UserEntity> optionalUsuarioEntity = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (optionalUsuarioEntity.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    UserEntity userEntity = optionalUsuarioEntity.get();
    userEntity.setMostrar(false);

    return modelMapper.map(usuarioRepository.save(userEntity), UserResponse.class);
  }
}
