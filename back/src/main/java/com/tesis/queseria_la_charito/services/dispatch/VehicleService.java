package com.tesis.queseria_la_charito.services.dispatch;

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
public class VehicleService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private VehicleRepository vehicleRepository;



  public List<VehicleResponse> getAll() {
    List<VehicleResponse> lstVehiclesResponse = new ArrayList<>();
    List<VehicleEntity>   lstVehiclesEntities = vehicleRepository.findAll();

    if (lstVehiclesEntities.isEmpty()) {
      return new ArrayList<>();
    }

    lstVehiclesEntities.forEach(entity -> {
      lstVehiclesResponse.add(modelMapper.map(entity, VehicleResponse.class));
    });

    return lstVehiclesResponse;
  }

  public VehicleResponse getById(Long id) {
    Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(id);
    if (vehicleEntityOptional.isEmpty()) {
      throw new EntityExistsException("No se ha encontrado el vehículo");
    }

    return modelMapper.map(vehicleEntityOptional.get(), VehicleResponse.class);
  }

  public VehicleResponse post(String plate) {
    Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findByPlate(plate);
    if (vehicleEntityOptional.isPresent()) {
      throw new EntityExistsException("Ya existe un vehículo con la misma matrícula");
    }

    VehicleEntity vehicleEntity = new VehicleEntity();
    vehicleEntity.setDisponible(true);
    vehicleEntity.setPlate(plate);

    return modelMapper.map(vehicleRepository.save(vehicleEntity), VehicleResponse.class);
  }

  public VehicleResponse delete(Long id) {
    Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(id);
    if(vehicleEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el vehículo");
    }

    VehicleEntity vehicleEntity = vehicleEntityOptional.get();

    if(!vehicleEntity.getLstDispatches().isEmpty()){
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
    Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(id);
    if (vehicleEntityOptional.isEmpty()) {
      throw new EntityExistsException("No se ha encontrado el vehículo");
    }

    VehicleEntity vehicleEntity = vehicleEntityOptional.get();
    vehicleEntity.setDisponible(!vehicleEntity.getDisponible());

    return modelMapper.map(vehicleRepository.save(vehicleEntity), VehicleResponse.class);
  }
}
