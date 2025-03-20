package com.tesis.queseria_la_charito.services.despachos;

import com.tesis.queseria_la_charito.dtos.response.dispatch.VehicleResponse;
import com.tesis.queseria_la_charito.entities.dispatch.VehicleEntity;
import com.tesis.queseria_la_charito.repositories.dispatch.VehicleRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class VehiculoService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private VehicleRepository vehicleRepository;



  public List<VehicleResponse> getAll() {
    List<VehicleResponse> lstVehiculosResponse = new ArrayList<>();
    List<VehicleEntity>   lstVehiculosEntities = vehicleRepository.findAll();

    if (lstVehiculosEntities.isEmpty()) {
      return new ArrayList<>();
    }

    lstVehiculosEntities.forEach(entity -> {
      lstVehiculosResponse.add(modelMapper.map(entity, VehicleResponse.class));
    });

    return lstVehiculosResponse;
  }

  public VehicleResponse getById(Long id) {
    Optional<VehicleEntity> vehiculoEntityOptional = vehicleRepository.findById(id);
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityExistsException("No se ha encontrado el vehículo");
    }

    return modelMapper.map(vehiculoEntityOptional.get(), VehicleResponse.class);
  }

  public VehicleResponse post(String matricula) {
    Optional<VehicleEntity> vehiculoEntityOptional = vehicleRepository.findByMatricula(matricula);
    if (vehiculoEntityOptional.isPresent()) {
      throw new EntityExistsException("Ya existe un vehículo con la misma matrícula");
    }

    VehicleEntity vehicleEntity = new VehicleEntity();
    vehicleEntity.setDisponible(true);
    vehicleEntity.setMatricula(matricula);

    return modelMapper.map(vehicleRepository.save(vehicleEntity), VehicleResponse.class);
  }

  public VehicleResponse delete(Long id) {
    Optional<VehicleEntity> vehiculoEntityOptional = vehicleRepository.findById(id);
    if(vehiculoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el vehículo");
    }

    VehicleEntity vehicleEntity = vehiculoEntityOptional.get();

    if(!vehicleEntity.getLstDespachos().isEmpty()){
      throw new IllegalStateException("No se puede eliminar el item porque tiene registros de despachos existentes");
    }

    try {
      vehicleRepository.delete(vehicleEntity);
      return modelMapper.map(vehicleEntity, VehicleResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public VehicleResponse put(Long id) {
    Optional<VehicleEntity> vehiculoEntityOptional = vehicleRepository.findById(id);
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityExistsException("No se ha encontrado el vehículo");
    }

    VehicleEntity vehicleEntity = vehiculoEntityOptional.get();
    vehicleEntity.setDisponible(!vehicleEntity.getDisponible());

    return modelMapper.map(vehicleRepository.save(vehicleEntity), VehicleResponse.class);
  }
}
