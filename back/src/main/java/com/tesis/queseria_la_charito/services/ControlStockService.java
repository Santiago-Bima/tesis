package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.stockControl.StockControlRequest;
import com.tesis.queseria_la_charito.dtos.request.stockControl.InsumoControlRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import com.tesis.queseria_la_charito.dtos.response.stockControl.ExpectedQuantityResponse;
import com.tesis.queseria_la_charito.dtos.response.stockControl.StockControlResponse;
import com.tesis.queseria_la_charito.dtos.response.stockControl.SupplyControlResponse;
import com.tesis.queseria_la_charito.dtos.response.stockControl.BatchControlResponse;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.stockControl.StockControlEntity;
import com.tesis.queseria_la_charito.entities.stockControl.InsumoControlEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import com.tesis.queseria_la_charito.models.ItemControlType;
import com.tesis.queseria_la_charito.models.CutType;
import com.tesis.queseria_la_charito.repositories.stockControl.StockControlRepository;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.stockControl.SupplyControlRepository;
import com.tesis.queseria_la_charito.repositories.user.UserRepository;
import com.tesis.queseria_la_charito.services.formulas.InsumosService;
import com.tesis.queseria_la_charito.services.lotes.LoteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ControlStockService {
  @Autowired
  private StockControlRepository repository;

  @Autowired
  private LoteService loteService;

  @Autowired
  private InsumosService insumoService;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private SupplyControlRepository supplyControlRepository;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private UserRepository usuarioRepository;



  public List<StockControlResponse> getAll(boolean validate) {
    List<StockControlResponse> responses = new ArrayList<>();

    List<StockControlEntity> controlStockEntities = repository.findAllByOrderByFechaDescIdDesc();
    if (controlStockEntities.isEmpty()) {
      return new ArrayList<>();
    }

    for (StockControlEntity controlEntity : controlStockEntities) {
      StockControlResponse response = modelMapper.map(controlEntity, StockControlResponse.class);

      List<SupplyControlResponse> insumoControlResponsesEsperado  = new ArrayList<>();
      List<InsumoControlEntity>   insumoControlEsperadoEntityList = supplyControlRepository.findByControlStockAndTipo(controlEntity, ItemControlType.Esperado.name());
      if (insumoControlEsperadoEntityList.isEmpty()) {
        insumoControlEsperadoEntityList = new ArrayList<>();
      }

      for (InsumoControlEntity insumoControlEntity : insumoControlEsperadoEntityList) {
        insumoControlResponsesEsperado.add(new SupplyControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
      }

      List<SupplyControlResponse> insumoControlResponsesObtenido  = new ArrayList<>();
      List<InsumoControlEntity>   insumoControlObtenidoEntityList = supplyControlRepository.findByControlStockAndTipo(controlEntity, ItemControlType.Obtenido.name());
      if (insumoControlObtenidoEntityList.isEmpty()) {
        insumoControlObtenidoEntityList = new ArrayList<>();
      }

      for (InsumoControlEntity insumoControlEntity : insumoControlObtenidoEntityList) {
        insumoControlResponsesObtenido.add(new SupplyControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
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

  public StockControlResponse post(StockControlRequest data) {
    StockControlEntity stockControlEntity = modelMapper.map(data, StockControlEntity.class);

    Optional<UserEntity> usuarioEntityOptional = usuarioRepository.findByUsername(data.getUsuario());
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    stockControlEntity.setUsuario(usuarioEntityOptional.get());
    stockControlEntity.setNuevo(true);

    List<InsumoControlEntity> insumoControlEntityListObtenidos = new ArrayList<>();

    for (InsumoControlRequest insumoControlRequest : data.getCantidadesInsumos()) {
      InsumoControlEntity insumoControlEntity = new InsumoControlEntity();

      Optional<ItemEntity> itemEntityOptional = itemRepository.findByNombre(insumoControlRequest.getInsumo());
      if (itemEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se ha encontrado el insumo");
      }

      insumoControlEntity.setInsumo(itemEntityOptional.get());
      insumoControlEntity.setCantidad(insumoControlRequest.getCantidad());
      insumoControlEntity.setControlStock(stockControlEntity);
      insumoControlEntity.setTipo(ItemControlType.Obtenido.name());

      insumoControlEntityListObtenidos.add(insumoControlEntity);
    }

    ExpectedQuantityResponse cantidadesEsperadasResponse = getEsperado();

    List<InsumoControlEntity> insumoControlEntityListEsperados = new ArrayList<>();

    for (SupplyControlResponse insumoControlResponse : cantidadesEsperadasResponse.getCantidadesInsumos()) {
      InsumoControlEntity insumoControlEntity = modelMapper.map(insumoControlResponse, InsumoControlEntity.class);

      Optional<ItemEntity> itemEntityOptional = itemRepository.findByNombre(insumoControlResponse.getInsumo());
      if (itemEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se ha encontrado el insumo");
      }

      insumoControlEntity.setInsumo(itemEntityOptional.get());
      insumoControlEntity.setControlStock(stockControlEntity);
      insumoControlEntity.setTipo(ItemControlType.Esperado.name());
      insumoControlEntityListEsperados.add(insumoControlEntity);
    }

    stockControlEntity.setCantidadCuartosEsperada(cantidadesEsperadasResponse.getCantidadCuartosEsperada());
    stockControlEntity.setCantidadEnterosEsperada(cantidadesEsperadasResponse.getCantidadEnterosEsperada());
    stockControlEntity.setCantidadMediosEsperada(cantidadesEsperadasResponse.getCantidadMediosEsperada());
    stockControlEntity.setControlesInsumosEsperados(insumoControlEntityListEsperados);

    stockControlEntity.setControlesInsumosObtenidos(insumoControlEntityListObtenidos);

    StockControlEntity   controlEntitySaved = repository.save(stockControlEntity);
    StockControlResponse response           = modelMapper.map(controlEntitySaved, StockControlResponse.class);

    List<SupplyControlResponse> insumoControlResponsesEsperado  = new ArrayList<>();
    List<InsumoControlEntity>   insumoControlEsperadoEntityList = controlEntitySaved.getControlesInsumosEsperados();
    if (insumoControlEsperadoEntityList.isEmpty()) {
      insumoControlEsperadoEntityList = new ArrayList<>();
    }

    for (InsumoControlEntity insumoControlEntity : insumoControlEsperadoEntityList) {
      insumoControlResponsesEsperado.add(new SupplyControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
    }

    List<SupplyControlResponse> insumoControlResponsesObtenido  = new ArrayList<>();
    List<InsumoControlEntity>   insumoControlObtenidoEntityList = controlEntitySaved.getControlesInsumosObtenidos();
    if (insumoControlObtenidoEntityList.isEmpty()) {
      insumoControlObtenidoEntityList = new ArrayList<>();
    }

    for (InsumoControlEntity insumoControlEntity : insumoControlObtenidoEntityList) {
      insumoControlResponsesObtenido.add(new SupplyControlResponse(insumoControlEntity.getInsumo().getNombre(), insumoControlEntity.getCantidad()));
    }

    response.setCantidadesInsumosEsperados(insumoControlResponsesEsperado);
    response.setCantidadesInsumosObtenidos(insumoControlResponsesObtenido);
    return response;
  }

  public ExpectedQuantityResponse getEsperado() {
    List<BatchControlResponse> listaLotes = loteService.getUnidades(null);
    if (listaLotes.isEmpty()) {
      return new ExpectedQuantityResponse();
    }

    ExpectedQuantityResponse cantidadesEsperadasResponse = getCantidadesEsperadasResponse();

    for (BatchControlResponse loteControl : listaLotes) {
      if (loteControl.getCorte() == null) {
        for (SupplyControlResponse insumoControlRequest : cantidadesEsperadasResponse.getCantidadesInsumos()) {
          if (insumoControlRequest.getInsumo().equals(loteControl.getItem())) {
            insumoControlRequest.setCantidad(insumoControlRequest.getCantidad() + loteControl.getUnidades());
            break;
          }
        }
      } else {

        if (loteControl.getCorte().equals(CutType.Whole.name())) {
          cantidadesEsperadasResponse.setCantidadEnterosEsperada(cantidadesEsperadasResponse.getCantidadEnterosEsperada() + loteControl.getUnidades());
        } else if (loteControl.getCorte().equals(CutType.Medio.name())) {
          cantidadesEsperadasResponse.setCantidadMediosEsperada(cantidadesEsperadasResponse.getCantidadMediosEsperada() + loteControl.getUnidades());
        } else {
          cantidadesEsperadasResponse.setCantidadCuartosEsperada(cantidadesEsperadasResponse.getCantidadCuartosEsperada() + loteControl.getUnidades());
        }
      }
    }

    return cantidadesEsperadasResponse;
  }

  private ExpectedQuantityResponse getCantidadesEsperadasResponse() {
    List<ItemResponse>          listaInsumos          = insumoService.getItems();
    List<SupplyControlResponse> listaInsumosControles = new ArrayList<>();

    for (ItemResponse item : listaInsumos) {
      SupplyControlResponse insumoControlRequest = new SupplyControlResponse();
      insumoControlRequest.setInsumo(item.getNombreItem());
      insumoControlRequest.setCantidad(0);

      listaInsumosControles.add(insumoControlRequest);
    }

    return new ExpectedQuantityResponse(0, 0, 0, listaInsumosControles);
  }
}
