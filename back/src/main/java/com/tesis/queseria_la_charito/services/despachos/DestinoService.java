package com.tesis.queseria_la_charito.services.despachos;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DestinationRequest;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DestinationResponse;
import com.tesis.queseria_la_charito.entities.dispatch.DestinationEntity;
import com.tesis.queseria_la_charito.repositories.despacho.DestinoRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DestinoService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private DestinoRepository destinoRepository;



  public DestinationResponse getById(Long id) {
    Optional<DestinationEntity> destinoEntityOptional = destinoRepository.findById(id);
    if(destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }

    return modelMapper.map(destinoEntityOptional.get(), DestinationResponse.class);
  }

  public List<DestinationResponse> getAll() {
    List<DestinationResponse> lstDestinosResponse = new ArrayList<>();
    List<DestinationEntity>   lstDestinosEntity   = destinoRepository.findAll();

    if(lstDestinosEntity.isEmpty()) {
      return new ArrayList<>();
    }

    lstDestinosEntity.forEach(destino -> {
      lstDestinosResponse.add(modelMapper.map(destino, DestinationResponse.class));
    });

    return lstDestinosResponse;
  }

  public DestinationResponse put(Long id, DestinationRequest destinoRequest) {
    Optional<DestinationEntity> existenteDestinoEntity = destinoRepository.findByCalleAndNumeroAndBarrio(destinoRequest.getCalle(), destinoRequest.getNumero(), destinoRequest.getBarrio());
    if(existenteDestinoEntity.isPresent()) {
      throw new EntityExistsException("Ya existe un destino con dichos datos");
    }

    Optional<DestinationEntity> destinoEntityOptional = destinoRepository.findById(id);
    if(destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }

    DestinationEntity destinationEntity = destinoEntityOptional.get();
    destinationEntity.setBarrio(destinoRequest.getBarrio());
    destinationEntity.setCalle(destinoRequest.getCalle());
    destinationEntity.setNumero(destinoRequest.getNumero());

    return modelMapper.map(destinoRepository.save(destinationEntity), DestinationResponse.class);
  }

  public DestinationResponse post(DestinationRequest destinoRequest) {
    DestinationEntity destinationEntity = modelMapper.map(destinoRequest, DestinationEntity.class);

    Optional<DestinationEntity> destinoEntityOptional = destinoRepository.findByCalleAndNumeroAndBarrio(destinationEntity.getCalle(), destinationEntity.getNumero(), destinationEntity.getBarrio());
    if(destinoEntityOptional.isPresent()) {
      throw new EntityExistsException("Ya existe el mismo destino");
    }

    return modelMapper.map(destinoRepository.save(destinationEntity), DestinationResponse.class);
  }

  public DestinationResponse delete(Long id) {
    Optional<DestinationEntity> destinoEntityOptional = destinoRepository.findById(id);
    if(destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }

    DestinationEntity destinationEntity = destinoEntityOptional.get();

    if(!destinationEntity.getLstDespachos().isEmpty()){
      throw new IllegalStateException("No se puede eliminar el item porque tiene registros de despachos existentes");
    }

    try {
      destinoRepository.delete(destinationEntity);
      return modelMapper.map(destinationEntity, DestinationResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
