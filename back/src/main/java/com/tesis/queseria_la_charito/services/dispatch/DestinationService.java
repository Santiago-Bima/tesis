package com.tesis.queseria_la_charito.services.dispatch;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DestinationRequest;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DestinationResponse;
import com.tesis.queseria_la_charito.entities.dispatch.DestinationEntity;
import com.tesis.queseria_la_charito.repositories.dispatch.DestinationRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DestinationService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private DestinationRepository destinationRepository;



  public DestinationResponse getById(Long id) {
    Optional<DestinationEntity> destinationEntityOptional = destinationRepository.findById(id);
    if(destinationEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }

    return modelMapper.map(destinationEntityOptional.get(), DestinationResponse.class);
  }

  public List<DestinationResponse> getAll() {
    List<DestinationResponse> lstDestinationsResponse = new ArrayList<>();
    List<DestinationEntity>   lstDestinationsEntity   = destinationRepository.findAll();

    if(lstDestinationsEntity.isEmpty()) {
      return new ArrayList<>();
    }

    lstDestinationsEntity.forEach(destination -> {
      lstDestinationsResponse.add(modelMapper.map(destination, DestinationResponse.class));
    });

    return lstDestinationsResponse;
  }

  public DestinationResponse put(Long id, DestinationRequest destinationRequest) {
    Optional<DestinationEntity> existentDestinationEntity = destinationRepository.findByStreetAndNumberAndNeighborhood(destinationRequest.getStreet(), destinationRequest.getNumber(), destinationRequest.getNeighborhood());
    if(existentDestinationEntity.isPresent()) {
      throw new EntityExistsException("Ya existe un destino con dichos datos");
    }

    Optional<DestinationEntity> destinationEntityOptional = destinationRepository.findById(id);
    if(destinationEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }

    DestinationEntity destinationEntity = destinationEntityOptional.get();
    destinationEntity.setNeighborhood(destinationRequest.getNeighborhood());
    destinationEntity.setStreet(destinationRequest.getStreet());
    destinationEntity.setNumber(destinationRequest.getNumber());

    return modelMapper.map(destinationRepository.save(destinationEntity), DestinationResponse.class);
  }

  public DestinationResponse post(DestinationRequest destinationRequest) {
    DestinationEntity destinationEntity = modelMapper.map(destinationRequest, DestinationEntity.class);

    Optional<DestinationEntity> destinoEntityOptional = destinationRepository.findByStreetAndNumberAndNeighborhood(destinationEntity.getStreet(), destinationEntity.getNumber(), destinationEntity.getNeighborhood());
    if(destinoEntityOptional.isPresent()) {
      throw new EntityExistsException("Ya existe el mismo destino");
    }

    return modelMapper.map(destinationRepository.save(destinationEntity), DestinationResponse.class);
  }

  public DestinationResponse delete(Long id) {
    Optional<DestinationEntity> destinationEntityOptional = destinationRepository.findById(id);
    if(destinationEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }

    DestinationEntity destinationEntity = destinationEntityOptional.get();

    if(!destinationEntity.getLstDispatches().isEmpty()){
      throw new IllegalStateException("No se puede eliminar el item porque tiene registros de despachos existentes");
    }

    try {
      destinationRepository.delete(destinationEntity);
      return modelMapper.map(destinationEntity, DestinationResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
