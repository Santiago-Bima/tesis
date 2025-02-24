package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.request.controlStock.ControlStockRequest;
import com.tesis.queseria_la_charito.dtos.request.controlStock.InsumoControlRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.CantidadesEsperadasResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.ControlStockResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.InsumoControlResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.LoteControlResponse;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.controlStock.ControlStockEntity;
import com.tesis.queseria_la_charito.entities.controlStock.InsumoControlEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import com.tesis.queseria_la_charito.models.TipoControlItem;
import com.tesis.queseria_la_charito.models.TipoCorte;
import com.tesis.queseria_la_charito.repositories.controlStock.ControlStockRepository;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.controlStock.InsumoControlRepository;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;
import com.tesis.queseria_la_charito.services.ControlStockService;
import com.tesis.queseria_la_charito.services.InsumoService;
import com.tesis.queseria_la_charito.services.LoteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ControlStockServiceImpl implements ControlStockService {
  @Autowired
  private ControlStockRepository repository;

  @Autowired
  private LoteService loteService;

  @Autowired
  private InsumoService insumoService;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private InsumoControlRepository insumoControlRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UsuarioRepository usuarioRepository;



  @Override
  public List<ControlStockResponse> getAll(boolean validate) {
    List<ControlStockResponse> responses = new ArrayList<>();

    List<ControlStockEntity> controlStockEntities = repository.findAllByOrderByFechaDescIdDesc();
    if (controlStockEntities.isEmpty()) {
      return new ArrayList<>();
    }

    for (ControlStockEntity controlEntity : controlStockEntities) {
      ControlStockResponse response = modelMapper.map(controlEntity, ControlStockResponse.class);

      List<InsumoControlResponse> insumoControlResponsesEsperado = new ArrayList<>();
      List<InsumoControlEntity> insumoControlEsperadoEntityList = insumoControlRepository.findByControlStockAndTipo(controlEntity, TipoControlItem.Esperado.name());
      if (insumoControlEsperadoEntityList.isEmpty()) {
        insumoControlEsperadoEntityList = new ArrayList<>();
      }

      for (InsumoControlEntity insumoControlEntity : insumoControlEsperadoEntityList) {
        insumoControlResponsesEsperado.add(new InsumoControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
      }

      List<InsumoControlResponse> insumoControlResponsesObtenido = new ArrayList<>();
      List<InsumoControlEntity> insumoControlObtenidoEntityList = insumoControlRepository.findByControlStockAndTipo(controlEntity, TipoControlItem.Obtenido.name());
      if (insumoControlObtenidoEntityList.isEmpty()) {
        insumoControlObtenidoEntityList = new ArrayList<>();
      }

      for (InsumoControlEntity insumoControlEntity : insumoControlObtenidoEntityList) {
        insumoControlResponsesObtenido.add(new InsumoControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
      }

      response.setCantidadesInsumosEsperados(insumoControlResponsesEsperado);
      response.setCantidadesInsumosObtenidos(insumoControlResponsesObtenido);

      responses.add(response);

      if (!validate) {
        controlEntity.setNuevo(false);
        repository.save(controlEntity);
      }
    }

    return responses;
  }

  @Override
  public ControlStockResponse post(ControlStockRequest data) {
    ControlStockEntity controlStockEntity = modelMapper.map(data, ControlStockEntity.class);

    Optional<UsuarioEntity> usuarioEntityOptional = usuarioRepository.findByUsername(data.getUsuario());
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    controlStockEntity.setUsuario(usuarioEntityOptional.get());
    controlStockEntity.setNuevo(true);

    List<InsumoControlEntity> insumoControlEntityListObtenidos = new ArrayList<>();

    for (InsumoControlRequest insumoControlRequest : data.getCantidadesInsumos()) {
      InsumoControlEntity insumoControlEntity = new InsumoControlEntity();

      Optional<ItemEntity> itemEntityOptional = itemRepository.findByNombre(insumoControlRequest.getInsumo());
      if (itemEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se ha encontrado el insumo");
      }

      insumoControlEntity.setInsumo(itemEntityOptional.get());
      insumoControlEntity.setCantidad(insumoControlRequest.getCantidad());
      insumoControlEntity.setControlStock(controlStockEntity);
      insumoControlEntity.setTipo(TipoControlItem.Obtenido.name());

      insumoControlEntityListObtenidos.add(insumoControlEntity);
    }

    CantidadesEsperadasResponse cantidadesEsperadasResponse = getEsperado();

    List<InsumoControlEntity> insumoControlEntityListEsperados = new ArrayList<>();

    for (InsumoControlResponse insumoControlResponse : cantidadesEsperadasResponse.getCantidadesInsumos()) {
      InsumoControlEntity insumoControlEntity = modelMapper.map(insumoControlResponse, InsumoControlEntity.class);

      Optional<ItemEntity> itemEntityOptional = itemRepository.findByNombre(insumoControlResponse.getInsumo());
      if (itemEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se ha encontrado el insumo");
      }

      insumoControlEntity.setInsumo(itemEntityOptional.get());
      insumoControlEntity.setControlStock(controlStockEntity);
      insumoControlEntity.setTipo(TipoControlItem.Esperado.name());
      insumoControlEntityListEsperados.add(insumoControlEntity);
    }

    controlStockEntity.setCantidadCuartosEsperada(cantidadesEsperadasResponse.getCantidadCuartosEsperada());
    controlStockEntity.setCantidadEnterosEsperada(cantidadesEsperadasResponse.getCantidadEnterosEsperada());
    controlStockEntity.setCantidadMediosEsperada(cantidadesEsperadasResponse.getCantidadMediosEsperada());
    controlStockEntity.setControlesInsumosEsperados(insumoControlEntityListEsperados);

    controlStockEntity.setControlesInsumosObtenidos(insumoControlEntityListObtenidos);

    ControlStockEntity controlEntitySaved = repository.save(controlStockEntity);
    ControlStockResponse response = modelMapper.map(controlEntitySaved, ControlStockResponse.class);

    List<InsumoControlResponse> insumoControlResponsesEsperado = new ArrayList<>();
    List<InsumoControlEntity> insumoControlEsperadoEntityList = controlEntitySaved.getControlesInsumosEsperados();
    if (insumoControlEsperadoEntityList.isEmpty()) {
      insumoControlEsperadoEntityList = new ArrayList<>();
    }

    for (InsumoControlEntity insumoControlEntity : insumoControlEsperadoEntityList) {
      insumoControlResponsesEsperado.add(new InsumoControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
    }

    List<InsumoControlResponse> insumoControlResponsesObtenido = new ArrayList<>();
    List<InsumoControlEntity> insumoControlObtenidoEntityList = controlEntitySaved.getControlesInsumosObtenidos();
    if (insumoControlObtenidoEntityList.isEmpty()) {
      insumoControlObtenidoEntityList = new ArrayList<>();
    }

    for (InsumoControlEntity insumoControlEntity : insumoControlObtenidoEntityList) {
      insumoControlResponsesObtenido.add(new InsumoControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
    }

    response.setCantidadesInsumosEsperados(insumoControlResponsesEsperado);
    response.setCantidadesInsumosObtenidos(insumoControlResponsesObtenido);
    return response;
  }

  @Override
  public CantidadesEsperadasResponse getEsperado() {
    List<LoteControlResponse> listaLotes = loteService.getUnidades(null);
    if (listaLotes.isEmpty()) {
      return new CantidadesEsperadasResponse();
    }

    CantidadesEsperadasResponse cantidadesEsperadasResponse = getCantidadesEsperadasResponse();

    for (LoteControlResponse loteControl : listaLotes) {
      if (loteControl.getCorte() == null) {
        for (InsumoControlResponse insumoControlRequest : cantidadesEsperadasResponse.getCantidadesInsumos()) {
          if (insumoControlRequest.getInsumo().equals(loteControl.getItem())) {
            insumoControlRequest.setCantidad(insumoControlRequest.getCantidad() + loteControl.getUnidades());
            break;
          }
        }
      } else {

        if (loteControl.getCorte().equals(TipoCorte.Entero.name())) {
          cantidadesEsperadasResponse.setCantidadEnterosEsperada(cantidadesEsperadasResponse.getCantidadEnterosEsperada() + loteControl.getUnidades());
        } else if (loteControl.getCorte().equals(TipoCorte.Medio.name())) {
          cantidadesEsperadasResponse.setCantidadMediosEsperada(cantidadesEsperadasResponse.getCantidadMediosEsperada() + loteControl.getUnidades());
        } else {
          cantidadesEsperadasResponse.setCantidadCuartosEsperada(cantidadesEsperadasResponse.getCantidadCuartosEsperada() + loteControl.getUnidades());
        }
      }
    }

    return cantidadesEsperadasResponse;
  }

  private CantidadesEsperadasResponse getCantidadesEsperadasResponse() {
    List<ItemResponse> listaInsumos = insumoService.getItems();
    List<InsumoControlResponse> listaInsumosControles = new ArrayList<>();

    for (ItemResponse item : listaInsumos) {
      InsumoControlResponse insumoControlRequest = new InsumoControlResponse();
      insumoControlRequest.setInsumo(item.getNombreItem());
      insumoControlRequest.setCantidad(0);

      listaInsumosControles.add(insumoControlRequest);
    }

    return new CantidadesEsperadasResponse(0, 0, 0, listaInsumosControles);
  }
}
