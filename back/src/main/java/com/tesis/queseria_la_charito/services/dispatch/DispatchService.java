package com.tesis.queseria_la_charito.services.dispatch;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchRequest;
import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchUpdateRequest;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DispatchResponse;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DestinationResponse;
import com.tesis.queseria_la_charito.dtos.response.dispatch.ReportDetailDispatch;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DispatchReportResponse;
import com.tesis.queseria_la_charito.entities.*;
import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DestinationEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchDetailEntity;
import com.tesis.queseria_la_charito.entities.dispatch.DispatchEntity;
import com.tesis.queseria_la_charito.entities.dispatch.VehicleEntity;
import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import com.tesis.queseria_la_charito.entities.production.processes.CutDetailEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import com.tesis.queseria_la_charito.models.DispatchStatus;
import com.tesis.queseria_la_charito.models.Status;
import com.tesis.queseria_la_charito.models.Cheese;
import com.tesis.queseria_la_charito.models.CutType;
import com.tesis.queseria_la_charito.repositories.*;
import com.tesis.queseria_la_charito.repositories.batch.BatchRepository;
import com.tesis.queseria_la_charito.repositories.dispatch.DispatchRepository;
import com.tesis.queseria_la_charito.repositories.dispatch.DestinationRepository;
import com.tesis.queseria_la_charito.repositories.dispatch.DispatchDetailRepository;
import com.tesis.queseria_la_charito.repositories.dispatch.VehicleRepository;
import com.tesis.queseria_la_charito.repositories.production.ProductionRepository;
import com.tesis.queseria_la_charito.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DispatchService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private DispatchRepository dispatchRepository;

  @Autowired
  private BatchRepository batchRepository;

  @Autowired
  private VehicleRepository vehicleRepository;

  @Autowired
  private DestinationRepository destinationRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private DispatchDetailRepository dispatchDetailRepository;

  @Autowired
  private ProductionRepository productionRepository;

  @Autowired
  private UserRepository userRepository;


//  TODO: Ver de cambiar el tipo de retorno
  public List<DispatchResponse> getByUser(String username) {
    List<DispatchResponse> lstDispatchResponses = new ArrayList<>();

    Optional<UserEntity> userEntityOptional = userRepository.findByUsernameAndShow(username, true);
    if (userEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    Optional<DispatchEntity> dispatchEntityOptional = dispatchRepository.findByResponsibleAndStatusNot(userEntityOptional.get(), DispatchStatus.Dispatched.name());
    if (dispatchEntityOptional.isEmpty()) {
      return new ArrayList<>();
    }

    DispatchResponse dispatchResponse = modelMapper.map(dispatchEntityOptional.get(), DispatchResponse.class);
    lstDispatchResponses.add(dispatchResponse);

    return lstDispatchResponses;
  }

  public List<DispatchResponse> getAll(LocalDate date, Long destinationId) {
    Optional<DestinationEntity> destinationEntityOptional = destinationRepository.findById(destinationId);
    if (destinationEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }

    List<DispatchEntity>   lstDispatchesEntities;
    List<DispatchResponse> lstDispatchResponse = new ArrayList<>();
    if(date != null) {
      lstDispatchesEntities = dispatchRepository.findByDestinationAndDate(destinationEntityOptional.get(), date);
    } else {
      lstDispatchesEntities = dispatchRepository.findByDestination(destinationEntityOptional.get());
    }
    if (lstDispatchesEntities.isEmpty()) {
      return new ArrayList<>();
    }

    lstDispatchesEntities.forEach(entity -> lstDispatchResponse.add(modelMapper.map(entity, DispatchResponse.class)));

    return lstDispatchResponse;
  }

  public DispatchResponse getById(Long id) {
    Optional<DispatchEntity> dispatchEntityOptional = dispatchRepository.findById(id);
    if (dispatchEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el despacho");
    }

    return modelMapper.map(dispatchEntityOptional.get(), DispatchResponse.class);
  }

  public DispatchResponse post(DispatchRequest dispatchRequest) {
    DispatchEntity dispatchEntity = new DispatchEntity();
    dispatchEntity.setLstDetails(new ArrayList<>());
    dispatchEntity.setDate(dispatchRequest.getDate());
    dispatchEntity.setCheese(dispatchRequest.getCheese());
    dispatchEntity.setStatus(DispatchStatus.PendingDelivery.name());

    Optional<UserEntity> userEntityOptional = userRepository.findByUsernameAndShow(dispatchRequest.getResponsible(), true);
    if (userEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    UserEntity userEntity = userEntityOptional.get();

    userEntity.setIsDispatching(true);
    dispatchEntity.setResponsible(userEntity);

    Optional<DestinationEntity> destinationEntityOptional = destinationRepository.findById(dispatchRequest.getDestination());
    if (destinationEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }
    dispatchEntity.setDestination(destinationEntityOptional.get());

    Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(dispatchRequest.getVehicle());
    if (vehicleEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el vehículo");
    }
    VehicleEntity vehicleEntity = vehicleEntityOptional.get();
    if (!vehicleEntity.getDisponible()) {
      throw new RuntimeException("El vehículo ya se encuentra en uso");
    }
    vehicleEntity.setDisponible(false);
    dispatchEntity.setVehicle(vehicleEntity);

    Optional<ItemEntity> cheeseTypeOptional = itemRepository.findByName(dispatchRequest.getCheese());
    if (cheeseTypeOptional.isEmpty()) {
      throw new EntityNotFoundException("No existe ese tipo de queso");
    }

    ItemEntity        cheeseType = cheeseTypeOptional.get();
    List<BatchEntity> lstBatchEntities     = batchRepository.findByItemAndStatusAndShow(cheeseType, Status.Finished.name(), true);
    if (lstBatchEntities.isEmpty()) {
      throw new RuntimeException("No hay ningún lote para despachar");
    }
    Integer totalWholes = dispatchRequest.getTotalWholes();
    Integer totalHalfs = dispatchRequest.getTotalHalfs();
    Integer totalQuarters = dispatchRequest.getTotalQuarters();
    int count = 0;

    dispatchEntity.setTotalQuantity(totalWholes + totalHalfs + totalQuarters);

    while (totalWholes > 0 || totalHalfs > 0 || totalQuarters > 0) {
      if (lstBatchEntities.size() - 1 < count) {
        throw new RuntimeException("No hay suficientes lstBatchEntities para despachar la cantidad de quesos requeridos");
      }
      DispatchDetailEntity dispatchDetailEntity = new DispatchDetailEntity();
      BatchEntity          batchEntity                 = lstBatchEntities.get(count);
      boolean               isModified            = false;

      Optional<ProductionEntity> productionEntityOptional = productionRepository.findByBatch(batchEntity);
      if (productionEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se encontró la elaboración del batchEntity");
      }
      ProductionEntity productionEntity = productionEntityOptional.get();

      AtomicInteger wholesBatch = new AtomicInteger(0);
      AtomicInteger halfsBatch = new AtomicInteger(0);
      AtomicInteger quartersBatch = new AtomicInteger(0);

      CutDetailEntity cutType = productionEntity.getCutDetail();
      if (cutType.getCut().equals(CutType.Whole.name())) {
        wholesBatch.addAndGet(batchEntity.getUnits());
      } else if (cutType.getCut().equals(CutType.Half.name())) {
        halfsBatch.addAndGet(batchEntity.getUnits());
      } else if (cutType.getCut().equals(CutType.Quarter.name())) {
        quartersBatch.addAndGet(batchEntity.getUnits());
      }

      if (wholesBatch.get() > 0) {
        int difference = totalWholes - wholesBatch.get();
        dispatchDetailEntity.setWholeQuantity(difference > 0 ? wholesBatch.get() : totalWholes);
        batchEntity.setUnits(batchEntity.getUnits() - (difference > 0 ? wholesBatch.get() : totalWholes));
        wholesBatch.set(difference >= 0 ? 0 : wholesBatch.get() - difference);
        totalWholes = Math.max(difference, 0);
        isModified = true;
      }

      if (halfsBatch.get() > 0) {
        int difference = totalHalfs - halfsBatch.get();
        dispatchDetailEntity.setHalfQuantity(difference > 0 ? halfsBatch.get() : totalHalfs);
        batchEntity.setUnits(batchEntity.getUnits() - (difference > 0 ? halfsBatch.get() : totalHalfs));
        totalHalfs = Math.max(difference, 0);
        halfsBatch.set(difference >= 0 ? 0 : halfsBatch.get() - difference);
        isModified = true;
      }

      if (quartersBatch.get() > 0) {
        int difference = totalQuarters - quartersBatch.get();
        dispatchDetailEntity.setQuarterQuantity(difference > 0 ? quartersBatch.get() : totalQuarters);
        batchEntity.setUnits(batchEntity.getUnits() - (difference > 0 ? quartersBatch.get() : totalQuarters));
        totalQuarters = Math.max(difference, 0);
        quartersBatch.set(difference >= 0 ? 0 : quartersBatch.get() - difference);
        isModified = true;
      }

      if (isModified) {
        if (wholesBatch.get() == 0 && halfsBatch.get() == 0 && quartersBatch.get() == 0) {
          batchEntity.setStatus(Status.Dispatched.name());
        }

        dispatchDetailEntity.setBatch(batchEntity);
        dispatchDetailEntity.setDispatch(dispatchEntity);
        dispatchEntity.getLstDetails().add(dispatchDetailEntity);
      }
      count ++;
    }

    vehicleRepository.save(vehicleEntity);
    userRepository.save(userEntity);
    return modelMapper.map(dispatchRepository.save(dispatchEntity), DispatchResponse.class);
  }

  public DispatchResponse put(DispatchUpdateRequest dispatchUpdateRequest, Long id) {
    Optional<DispatchEntity> dispatchEntityOptional = dispatchRepository.findById(id);
    if(dispatchEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }
    DispatchEntity dispatchEntity = dispatchEntityOptional.get();

    if (!dispatchEntity.getStatus().equals(DispatchStatus.PendingDelivery.name())) {
      throw new RuntimeException("No se puede modificar un despacho que está entregado o en proceso");
    }

    Optional<VehicleEntity> vehicleEntityOptional = vehicleRepository.findById(dispatchUpdateRequest.getIdVehicle());
    if (vehicleEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el vehículo");
    }
    VehicleEntity vehicleEntity = vehicleEntityOptional.get();
    if (!vehicleEntity.getDisponible()) {
      throw new RuntimeException("El vehículo ya se encuentra en uso");
    }
    dispatchEntity.setVehicle(vehicleEntityOptional.get());

    Optional<DestinationEntity> destinationEntityOptional = destinationRepository.findById(dispatchUpdateRequest.getIdDestination());
    if (destinationEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }
    dispatchEntity.setDestination(destinationEntityOptional.get());

    return modelMapper.map(dispatchRepository.save(dispatchEntity), DispatchResponse.class);
  }

  public DispatchResponse delete(Long id) {
    Optional<DispatchEntity> dispatchEntityOptional = dispatchRepository.findById(id);
    if (dispatchEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }

    DispatchEntity dispatchEntity = dispatchEntityOptional.get();

    if (dispatchEntity.getStatus().equals(DispatchStatus.Delivered.name())) {
      throw new RuntimeException("No se puede eliminar un despacho que está siendo entregado");
    }
    if (dispatchEntity.getStatus().equals(DispatchStatus.PendingDelivery.name())) {
      dispatchEntity.getResponsible().setIsDispatching(false);
      userRepository.save(dispatchEntity.getResponsible());
      dispatchEntity.getVehicle().setDisponible(true);
      vehicleRepository.save(dispatchEntity.getVehicle());
    }

    if (!dispatchEntity.getLstDetails().isEmpty()) {
      dispatchEntity.getLstDetails().forEach(detailEntity -> {
        Optional<BatchEntity> batchEntityOptional = batchRepository.findById(detailEntity.getBatch().getId());
        if (batchEntityOptional.isEmpty()) {
          throw new EntityNotFoundException("No se ha encontrado el lote del detailEntity");
        }

        if (dispatchEntity.getStatus().equals(DispatchStatus.PendingDelivery.name())) {
          if (batchEntityOptional.get().getProduction().getCutDetail().getCut().equals(CutType.Whole.name())) {
            batchEntityOptional.get().setUnits(batchEntityOptional.get().getUnits() + detailEntity.getWholeQuantity());
          } else  if (batchEntityOptional.get().getProduction().getCutDetail().getCut().equals(CutType.Half.name())) {
            batchEntityOptional.get().setUnits(batchEntityOptional.get().getUnits() + detailEntity.getHalfQuantity());
          } else {
            batchEntityOptional.get().setUnits(batchEntityOptional.get().getUnits() + detailEntity.getQuarterQuantity());
          }

          batchRepository.save(batchEntityOptional.get());
          dispatchDetailRepository.delete(detailEntity);
        } else {
          if (batchEntityOptional.get().getUnits() > 0) {
            throw new IllegalStateException("No se puede eliminar ya que uno de los lotes que se usaron aún posee unidades");
          }
        }
      });
    }

    try{
      dispatchRepository.delete(dispatchEntity);
      return modelMapper.map(dispatchEntity, DispatchResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public DispatchReportResponse generateReport(LocalDate beginningDate, LocalDate endDate) {
    DispatchReportResponse dispatchReportResponse = new DispatchReportResponse();
    dispatchReportResponse.setDispatchesQuantity(0);
    dispatchReportResponse.setTotalBarraDispatched(0);
    dispatchReportResponse.setTotalQuarterCremosoDispatched(0);
    dispatchReportResponse.setTotalPategrasDispatched(0);
    dispatchReportResponse.setTotalHalfCremosoDispatched(0);
    dispatchReportResponse.setTotalWholeCremosoDispatched(0);
    dispatchReportResponse.setTotalUnitsDispatched(0);

    List<DestinationEntity> lstDestinationEntities = destinationRepository.findAll();
    if (lstDestinationEntities.isEmpty()) {
      lstDestinationEntities = new ArrayList<>();
    }


    List<ReportDetailDispatch> lstReportDetailDispatch = new ArrayList<>();
    dispatchReportResponse.setLstDetails(lstReportDetailDispatch);
    for (DestinationEntity destinationEntity : lstDestinationEntities) {
      ReportDetailDispatch reportDetailDispatch = new ReportDetailDispatch();
      reportDetailDispatch.setDestination(modelMapper.map(destinationEntity, DestinationResponse.class));
      reportDetailDispatch.setPategrasQuantity(0);
      reportDetailDispatch.setBarraQuantity(0);
      reportDetailDispatch.setCremosoHalfQuantity(0);
      reportDetailDispatch.setCremosoQuarterQuantity(0);
      reportDetailDispatch.setCremosoWholeQuantity(0);

      List<DispatchEntity> lstDispatchEntities = dispatchRepository.findByDestinationAndDateBetween(destinationEntity, beginningDate, endDate);
      for (DispatchEntity dispatchEntity : lstDispatchEntities) {
        dispatchReportResponse.setDispatchesQuantity(dispatchReportResponse.getDispatchesQuantity() + 1);
        dispatchReportResponse.setTotalUnitsDispatched(dispatchReportResponse.getTotalUnitsDispatched() + dispatchEntity.getTotalQuantity());


        if (dispatchEntity.getCheese().equals(Cheese.Pategras.name())) {
          for (DispatchDetailEntity dispatchDetailEntity : dispatchEntity.getLstDetails()) {
            dispatchReportResponse.setTotalPategrasDispatched(dispatchReportResponse.getTotalPategrasDispatched() + dispatchDetailEntity.getWholeQuantity());
            reportDetailDispatch.setPategrasQuantity(reportDetailDispatch.getPategrasQuantity() + dispatchDetailEntity.getWholeQuantity());
          }
        } else if (dispatchEntity.getCheese().equals(Cheese.Barra.name())) {
          for (DispatchDetailEntity dispatchDetailEntity : dispatchEntity.getLstDetails()) {
            dispatchReportResponse.setTotalBarraDispatched(dispatchReportResponse.getTotalBarraDispatched() + dispatchDetailEntity.getWholeQuantity());
            reportDetailDispatch.setBarraQuantity(reportDetailDispatch.getBarraQuantity() + dispatchDetailEntity.getWholeQuantity());
          }
        } else {
          for (DispatchDetailEntity dispatchDetailEntity : dispatchEntity.getLstDetails()) {
            if (dispatchDetailEntity.getWholeQuantity() != null) {
              dispatchReportResponse.setTotalWholeCremosoDispatched(dispatchReportResponse.getTotalWholeCremosoDispatched() + dispatchDetailEntity.getWholeQuantity());
              reportDetailDispatch.setCremosoWholeQuantity(reportDetailDispatch.getCremosoWholeQuantity() + dispatchDetailEntity.getWholeQuantity());
            }
            if (dispatchDetailEntity.getHalfQuantity() != null) {
              dispatchReportResponse.setTotalHalfCremosoDispatched(dispatchReportResponse.getTotalHalfCremosoDispatched() + dispatchDetailEntity.getHalfQuantity());
              reportDetailDispatch.setCremosoHalfQuantity(reportDetailDispatch.getCremosoHalfQuantity() + dispatchDetailEntity.getHalfQuantity());
            }
            if (dispatchDetailEntity.getQuarterQuantity() != null) {
              dispatchReportResponse.setTotalQuarterCremosoDispatched(dispatchReportResponse.getTotalQuarterCremosoDispatched() + dispatchDetailEntity.getQuarterQuantity());
              reportDetailDispatch.setCremosoQuarterQuantity(reportDetailDispatch.getCremosoQuarterQuantity() + dispatchDetailEntity.getQuarterQuantity());
            }
          }
        }

      }


      dispatchReportResponse.getLstDetails().add(reportDetailDispatch);
    }

    dispatchReportResponse.setLstDetails(lstReportDetailDispatch);
    return dispatchReportResponse;
  }

  public DispatchResponse changeEstado(Long id) {
    Optional<DispatchEntity> despachoEntityOptional = dispatchRepository.findById(id);
    if(despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }
    DispatchEntity dispatchEntity = despachoEntityOptional.get();


    VehicleEntity vehicleEntity = dispatchEntity.getVehicle();
    UserEntity    userEntity    = dispatchEntity.getResponsible();

    if (dispatchEntity.getStatus().equals(DispatchStatus.PendingDelivery.name())) {
      dispatchEntity.setStatus(DispatchStatus.Delivered.name());
    } else if (dispatchEntity.getStatus().equals(DispatchStatus.Delivered.name())) {
      dispatchEntity.setStatus(DispatchStatus.Dispatched.name());
      vehicleEntity.setDisponible(true);
      userEntity.setIsDispatching(false);

      userRepository.save(userEntity);
      vehicleRepository.save(vehicleEntity);
    }


    return modelMapper.map(dispatchRepository.save(dispatchEntity), DispatchResponse.class);
  }
}
