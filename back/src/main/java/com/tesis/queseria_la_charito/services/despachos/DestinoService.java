package com.tesis.queseria_la_charito.services.despachos;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DestinationRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DestinoResponse;
import com.tesis.queseria_la_charito.entities.despacho.DestinoEntity;
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



  public DestinoResponse getById(Long id) {
    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findById(id);
    if(destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }

    return modelMapper.map(destinoEntityOptional.get(), DestinoResponse.class);
  }

  public List<DestinoResponse> getAll() {
    List<DestinoResponse> lstDestinosResponse = new ArrayList<>();
    List<DestinoEntity> lstDestinosEntity = destinoRepository.findAll();

    if(lstDestinosEntity.isEmpty()) {
      return new ArrayList<>();
    }

    lstDestinosEntity.forEach(destino -> {
      lstDestinosResponse.add(modelMapper.map(destino, DestinoResponse.class));
    });

    return lstDestinosResponse;
  }

  public DestinoResponse put(Long id, DestinationRequest destinoRequest) {
    Optional<DestinoEntity> existenteDestinoEntity = destinoRepository.findByCalleAndNumeroAndBarrio(destinoRequest.getCalle(), destinoRequest.getNumero(), destinoRequest.getBarrio());
    if(existenteDestinoEntity.isPresent()) {
      throw new EntityExistsException("Ya existe un destino con dichos datos");
    }

    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findById(id);
    if(destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }

    DestinoEntity destinoEntity = destinoEntityOptional.get();
    destinoEntity.setBarrio(destinoRequest.getBarrio());
    destinoEntity.setCalle(destinoRequest.getCalle());
    destinoEntity.setNumero(destinoRequest.getNumero());

    return modelMapper.map(destinoRepository.save(destinoEntity), DestinoResponse.class);
  }

  public DestinoResponse post(DestinationRequest destinoRequest) {
    DestinoEntity destinoEntity = modelMapper.map(destinoRequest, DestinoEntity.class);

    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findByCalleAndNumeroAndBarrio(destinoEntity.getCalle(), destinoEntity.getNumero(), destinoEntity.getBarrio());
    if(destinoEntityOptional.isPresent()) {
      throw new EntityExistsException("Ya existe el mismo destino");
    }

    return modelMapper.map(destinoRepository.save(destinoEntity), DestinoResponse.class);
  }

  public DestinoResponse delete(Long id) {
    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findById(id);
    if(destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }

    DestinoEntity destinoEntity = destinoEntityOptional.get();

    if(!destinoEntity.getLstDespachos().isEmpty()){
      throw new IllegalStateException("No se puede eliminar el item porque tiene registros de despachos existentes");
    }

    try {
      destinoRepository.delete(destinoEntity);
      return modelMapper.map(destinoEntity, DestinoResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
