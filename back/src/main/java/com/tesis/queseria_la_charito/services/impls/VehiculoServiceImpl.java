package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.response.despacho.VehiculoResponse;
import com.tesis.queseria_la_charito.entities.despacho.VehiculoEntity;
import com.tesis.queseria_la_charito.repositories.despacho.VehiculoRepository;
import com.tesis.queseria_la_charito.services.VehiculoService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculoServiceImpl implements VehiculoService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private VehiculoRepository vehiculoRepository;

  @Override
  public List<VehiculoResponse> getAll() {
    List<VehiculoResponse> lstVehiculosResponse = new ArrayList<>();
    List<VehiculoEntity> lstVehiculosEntities = vehiculoRepository.findAll();

    if (lstVehiculosEntities.isEmpty()) {
      return new ArrayList<>();
    }

    lstVehiculosEntities.forEach(entity -> {
      lstVehiculosResponse.add(modelMapper.map(entity, VehiculoResponse.class));
    });

    return lstVehiculosResponse;
  }

  @Override
  public VehiculoResponse getById(Long id) {
    Optional<VehiculoEntity> vehiculoEntityOptional = vehiculoRepository.findById(id);
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityExistsException("No se ha encontrado el vehículo");
    }

    return modelMapper.map(vehiculoEntityOptional.get(), VehiculoResponse.class);
  }

  @Override
  public VehiculoResponse post(String matricula) {
    Optional<VehiculoEntity> vehiculoEntityOptional = vehiculoRepository.findByMatricula(matricula);
    if (vehiculoEntityOptional.isPresent()) {
      throw new EntityExistsException("Ya existe un vehículo con la misma matrícula");
    }

    VehiculoEntity vehiculoEntity = new VehiculoEntity();
    vehiculoEntity.setDisponible(true);
    vehiculoEntity.setMatricula(matricula);

    return modelMapper.map(vehiculoRepository.save(vehiculoEntity), VehiculoResponse.class);
  }

  @Override
  public VehiculoResponse delete(Long id) {
    Optional<VehiculoEntity> vehiculoEntityOptional = vehiculoRepository.findById(id);
    if(vehiculoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el vehículo");
    }

    VehiculoEntity vehiculoEntity = vehiculoEntityOptional.get();

    if(!vehiculoEntity.getLstDespachos().isEmpty()){
      throw new IllegalStateException("No se puede eliminar el item porque tiene registros de despachos existentes");
    }

    try {
      vehiculoRepository.delete(vehiculoEntity);
      return modelMapper.map(vehiculoEntity, VehiculoResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public VehiculoResponse put(Long id) {
    Optional<VehiculoEntity> vehiculoEntityOptional = vehiculoRepository.findById(id);
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityExistsException("No se ha encontrado el vehículo");
    }

    VehiculoEntity vehiculoEntity = vehiculoEntityOptional.get();
    vehiculoEntity.setDisponible(!vehiculoEntity.getDisponible());

    return modelMapper.map(vehiculoRepository.save(vehiculoEntity), VehiculoResponse.class);
  }
}
