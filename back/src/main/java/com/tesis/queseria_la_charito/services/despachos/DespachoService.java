package com.tesis.queseria_la_charito.services.despachos;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchRequest;
import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchUpdateRequest;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DispatchResponse;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DestinationResponse;
import com.tesis.queseria_la_charito.dtos.response.dispatch.ReportDetailDespacho;
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
import com.tesis.queseria_la_charito.repositories.despacho.DespachoRepository;
import com.tesis.queseria_la_charito.repositories.despacho.DestinoRepository;
import com.tesis.queseria_la_charito.repositories.despacho.DetalleDespachoRepository;
import com.tesis.queseria_la_charito.repositories.despacho.VehiculoRepository;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;
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
public class DespachoService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private DespachoRepository despachoRepository;

  @Autowired
  private LoteRepository loteRepository;

  @Autowired
  private VehiculoRepository vehiculoRepository;

  @Autowired
  private DestinoRepository destinoRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private DetalleDespachoRepository detalleDespachoRepository;

  @Autowired
  private ElaboracionRepository elaboracionRepository;

  @Autowired
  private UsuarioRepository usuarioRepository;


//  TODO: Ver de cambiar el tipo de retorno
  public List<DispatchResponse> getByUser(String username) {
    List<DispatchResponse> despachoResponses = new ArrayList<>();

    Optional<UserEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    Optional<DispatchEntity> despachoEntityOptional = despachoRepository.findByUsuarioAndEstadoNot(usuarioEntityOptional.get(), DispatchStatus.Despachado.name());
    if (despachoEntityOptional.isEmpty()) {
      return new ArrayList<>();
    }

    DispatchResponse despachoResponse = modelMapper.map(despachoEntityOptional.get(), DispatchResponse.class);
    despachoResponses.add(despachoResponse);

    return despachoResponses;
  }

  public List<DispatchResponse> getAll(LocalDate fecha, Long destinoId) {
    Optional<DestinationEntity> destinoEntityOptional = destinoRepository.findById(destinoId);
    if (destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }

    List<DispatchEntity>   lstDespachosEntities;
    List<DispatchResponse> lstDespachoResponse = new ArrayList<>();
    if(fecha != null) {
      lstDespachosEntities = despachoRepository.findByDestinoAndFecha(destinoEntityOptional.get(), fecha);
    } else {
      lstDespachosEntities = despachoRepository.findByDestino(destinoEntityOptional.get());
    }
    if (lstDespachosEntities.isEmpty()) {
      return new ArrayList<>();
    }

    lstDespachosEntities.forEach(entity -> lstDespachoResponse.add(modelMapper.map(entity, DispatchResponse.class)));

    return lstDespachoResponse;
  }

  public DispatchResponse getById(Long id) {
    Optional<DispatchEntity> despachoEntityOptional = despachoRepository.findById(id);
    if (despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el despacho");
    }

    return modelMapper.map(despachoEntityOptional.get(), DispatchResponse.class);
  }

  public DispatchResponse post(DispatchRequest despachoRequest) {
    DispatchEntity dispatchEntity = new DispatchEntity();
    dispatchEntity.setLstDetallesDespacho(new ArrayList<>());
    dispatchEntity.setFecha(despachoRequest.getFecha());
    dispatchEntity.setQueso(despachoRequest.getQueso());
    dispatchEntity.setEstado(DispatchStatus.PorEntregar.name());

    Optional<UserEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(despachoRequest.getUsuario(), true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    UserEntity userEntity = usuarioEntityOptional.get();

    userEntity.setIsDispatching(true);
    dispatchEntity.setUsuario(userEntity);

    Optional<DestinationEntity> destinoEntityOptional = destinoRepository.findById(despachoRequest.getDestino());
    if (destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }
    dispatchEntity.setDestino(destinoEntityOptional.get());

    Optional<VehicleEntity> vehiculoEntityOptional = vehiculoRepository.findById(despachoRequest.getVehiculo());
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el vehículo");
    }
    VehicleEntity vehicleEntity = vehiculoEntityOptional.get();
    if (!vehicleEntity.getDisponible()) {
      throw new RuntimeException("El vehículo ya se encuentra en uso");
    }
    vehicleEntity.setDisponible(false);
    dispatchEntity.setVehiculo(vehicleEntity);

    Optional<ItemEntity> tipoQuesoOptional = itemRepository.findByNombre(despachoRequest.getQueso());
    if (tipoQuesoOptional.isEmpty()) {
      throw new EntityNotFoundException("No existe ese tipo de queso");
    }

    ItemEntity        tipoQueso = tipoQuesoOptional.get();
    List<BatchEntity> lotes     = loteRepository.findByItemAndEstadoAndMostrar(tipoQueso, Status.Terminado.name(), true);
    if (lotes.isEmpty()) {
      throw new RuntimeException("No hay ningún lote para despachar");
    }
    Integer totalEntero = despachoRequest.getTotalEnteros();
    Integer totalMedio = despachoRequest.getTotalMedios();
    Integer totalCuartos = despachoRequest.getTotalCuartos();
    int contador = 0;

    dispatchEntity.setCantidadTotal(totalEntero + totalMedio + totalCuartos);

    while (totalEntero > 0 || totalMedio > 0 || totalCuartos > 0) {
      if (lotes.size() - 1 < contador) {
        throw new RuntimeException("No hay suficientes lotes para despachar la cantidad de quesos requeridos");
      }
      DispatchDetailEntity dispatchDetailEntity = new DispatchDetailEntity();
      BatchEntity          lote                 = lotes.get(contador);
      boolean               modificado            = false;

      Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findByLote(lote);
      if (elaboracionEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se encontró la elaboración del lote");
      }
      ProductionEntity elaboracion = elaboracionEntityOptional.get();

      AtomicInteger enterosLote = new AtomicInteger(0);
      AtomicInteger mediosLote = new AtomicInteger(0);
      AtomicInteger cuartosLote = new AtomicInteger(0);

      CutDetailEntity corte = elaboracion.getDetalleCorte();
      if (corte.getCorte().equals(CutType.Whole.name())) {
        enterosLote.addAndGet(lote.getUnidades());
      } else if (corte.getCorte().equals(CutType.Medio.name())) {
        mediosLote.addAndGet(lote.getUnidades());
      } else if (corte.getCorte().equals(CutType.Cuarto.name())) {
        cuartosLote.addAndGet(lote.getUnidades());
      }

      if (enterosLote.get() > 0) {
        int diferencia = totalEntero - enterosLote.get();
        dispatchDetailEntity.setCantidadEnteros(diferencia > 0 ? enterosLote.get() : totalEntero);
        lote.setUnidades(lote.getUnidades() - (diferencia > 0 ? enterosLote.get() : totalEntero));
        enterosLote.set(diferencia >= 0 ? 0 : enterosLote.get() - diferencia);
        totalEntero = Math.max(diferencia, 0);
        modificado = true;
      }

      if (mediosLote.get() > 0) {
        int diferencia = totalMedio - mediosLote.get();
        dispatchDetailEntity.setCantidadMedios(diferencia > 0 ? mediosLote.get() : totalMedio);
        lote.setUnidades(lote.getUnidades() - (diferencia > 0 ? mediosLote.get() : totalMedio));
        totalMedio = Math.max(diferencia, 0);
        mediosLote.set(diferencia >= 0 ? 0 : mediosLote.get() - diferencia);
        modificado = true;
      }

      if (cuartosLote.get() > 0) {
        int diferencia = totalCuartos - cuartosLote.get();
        dispatchDetailEntity.setCantidadCuartos(diferencia > 0 ? cuartosLote.get() : totalCuartos);
        lote.setUnidades(lote.getUnidades() - (diferencia > 0 ? cuartosLote.get() : totalCuartos));
        totalCuartos = Math.max(diferencia, 0);
        cuartosLote.set(diferencia >= 0 ? 0 : cuartosLote.get() - diferencia);
        modificado = true;
      }

      if (modificado) {
        if (enterosLote.get() == 0 && mediosLote.get() == 0 && cuartosLote.get() == 0) {
          lote.setEstado(Status.Despachado.name());
        }

        dispatchDetailEntity.setLote(lote);
        dispatchDetailEntity.setDespacho(dispatchEntity);
        dispatchEntity.getLstDetallesDespacho().add(dispatchDetailEntity);
      }
      contador ++;
    }

    vehiculoRepository.save(vehicleEntity);
    usuarioRepository.save(userEntity);
    return modelMapper.map(despachoRepository.save(dispatchEntity), DispatchResponse.class);
  }

  public DispatchResponse put(DispatchUpdateRequest despachoRequest, Long id) {
    Optional<DispatchEntity> despachoEntityOptional = despachoRepository.findById(id);
    if(despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }
    DispatchEntity dispatchEntity = despachoEntityOptional.get();

    if (!dispatchEntity.getEstado().equals(DispatchStatus.PorEntregar.name())) {
      throw new RuntimeException("No se puede modificar un despacho que está entregado o en proceso");
    }

    Optional<VehicleEntity> vehiculoEntityOptional = vehiculoRepository.findById(despachoRequest.getIdVehiculo());
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el vehículo");
    }
    VehicleEntity vehicleEntity = vehiculoEntityOptional.get();
    if (!vehicleEntity.getDisponible()) {
      throw new RuntimeException("El vehículo ya se encuentra en uso");
    }
    dispatchEntity.setVehiculo(vehiculoEntityOptional.get());

    Optional<DestinationEntity> destinoEntityOptional = destinoRepository.findById(despachoRequest.getIdDestino());
    if (destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }
    dispatchEntity.setDestino(destinoEntityOptional.get());

    return modelMapper.map(despachoRepository.save(dispatchEntity), DispatchResponse.class);
  }

  public DispatchResponse delete(Long id) {
    Optional<DispatchEntity> despachoEntityOptional = despachoRepository.findById(id);
    if (despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }

    DispatchEntity dispatchEntity = despachoEntityOptional.get();

    if (dispatchEntity.getEstado().equals(DispatchStatus.Entregando.name())) {
      throw new RuntimeException("No se puede eliminar un despacho que está siendo entregado");
    }
    if (dispatchEntity.getEstado().equals(DispatchStatus.PorEntregar.name())) {
      dispatchEntity.getUsuario().setIsDispatching(false);
      usuarioRepository.save(dispatchEntity.getUsuario());
      dispatchEntity.getVehiculo().setDisponible(true);
      vehiculoRepository.save(dispatchEntity.getVehiculo());
    }

    if (!dispatchEntity.getLstDetallesDespacho().isEmpty()) {
      dispatchEntity.getLstDetallesDespacho().forEach(detalle -> {
        Optional<BatchEntity> loteEntityOptional = loteRepository.findById(detalle.getLote().getId());
        if (loteEntityOptional.isEmpty()) {
          throw new EntityNotFoundException("No se ha encontrado el lote del detalle");
        }

        if (dispatchEntity.getEstado().equals(DispatchStatus.PorEntregar.name())) {
          if (loteEntityOptional.get().getElaboracion().getDetalleCorte().getCorte().equals(CutType.Whole.name())) {
            loteEntityOptional.get().setUnidades(loteEntityOptional.get().getUnidades() + detalle.getCantidadEnteros());
          } else  if (loteEntityOptional.get().getElaboracion().getDetalleCorte().getCorte().equals(CutType.Medio.name())) {
            loteEntityOptional.get().setUnidades(loteEntityOptional.get().getUnidades() + detalle.getCantidadMedios());
          } else {
            loteEntityOptional.get().setUnidades(loteEntityOptional.get().getUnidades() + detalle.getCantidadCuartos());
          }

          loteRepository.save(loteEntityOptional.get());
          detalleDespachoRepository.delete(detalle);
        } else {
          if (loteEntityOptional.get().getUnidades() > 0) {
            throw new IllegalStateException("No se puede eliminar ya que uno de los lotes que se usaron aún posee unidades");
          }
        }
      });
    }

    try{
      despachoRepository.delete(dispatchEntity);
      return modelMapper.map(dispatchEntity, DispatchResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public DispatchReportResponse generateInforme(LocalDate fechaInicio, LocalDate fechaFin) {
    DispatchReportResponse informeDespachoResponse = new DispatchReportResponse();
    informeDespachoResponse.setCantidadDespachos(0);
    informeDespachoResponse.setCantidadTotalBarra(0);
    informeDespachoResponse.setCantidadTotalCuartosCremoso(0);
    informeDespachoResponse.setCantidadTotalPategras(0);
    informeDespachoResponse.setCantidadTotalMediosCremoso(0);
    informeDespachoResponse.setCantidadTotalEnterosCremoso(0);
    informeDespachoResponse.setTotalUnidadesDespachadas(0);

    List<DestinationEntity> destinoEntities = destinoRepository.findAll();
    if (destinoEntities.isEmpty()) {
      destinoEntities = new ArrayList<>();
    }


    List<ReportDetailDespacho> detalleInformeDespachos = new ArrayList<>();
    informeDespachoResponse.setDetallesDespacho(detalleInformeDespachos);
    for (DestinationEntity destinationEntity : destinoEntities) {
      ReportDetailDespacho detalleInformeDespacho = new ReportDetailDespacho();
      detalleInformeDespacho.setDestino(modelMapper.map(destinationEntity, DestinationResponse.class));
      detalleInformeDespacho.setCantidadPategras(0);
      detalleInformeDespacho.setCantidadBarra(0);
      detalleInformeDespacho.setCantidadMediosCremoso(0);
      detalleInformeDespacho.setCantidadCuartosCremoso(0);
      detalleInformeDespacho.setCantidadEnterosCremoso(0);

      List<DispatchEntity> despachoEntities = despachoRepository.findByDestinoAndFechaBetween(destinationEntity, fechaInicio, fechaFin);
      for (DispatchEntity dispatchEntity : despachoEntities) {
        informeDespachoResponse.setCantidadDespachos(informeDespachoResponse.getCantidadDespachos() + 1);
        informeDespachoResponse.setTotalUnidadesDespachadas(informeDespachoResponse.getTotalUnidadesDespachadas() + dispatchEntity.getCantidadTotal());


        if (dispatchEntity.getQueso().equals(Cheese.Pategras.name())) {
          for (DispatchDetailEntity dispatchDetailEntity : dispatchEntity.getLstDetallesDespacho()) {
            informeDespachoResponse.setCantidadTotalPategras(informeDespachoResponse.getCantidadTotalPategras() + dispatchDetailEntity.getCantidadEnteros());
            detalleInformeDespacho.setCantidadPategras(detalleInformeDespacho.getCantidadPategras() + dispatchDetailEntity.getCantidadEnteros());
          }
        } else if (dispatchEntity.getQueso().equals(Cheese.Barra.name())) {
          for (DispatchDetailEntity dispatchDetailEntity : dispatchEntity.getLstDetallesDespacho()) {
            informeDespachoResponse.setCantidadTotalBarra(informeDespachoResponse.getCantidadTotalBarra() + dispatchDetailEntity.getCantidadEnteros());
            detalleInformeDespacho.setCantidadBarra(detalleInformeDespacho.getCantidadBarra() + dispatchDetailEntity.getCantidadEnteros());
          }
        } else {
          for (DispatchDetailEntity dispatchDetailEntity : dispatchEntity.getLstDetallesDespacho()) {
            if (dispatchDetailEntity.getCantidadEnteros() != null) {
              informeDespachoResponse.setCantidadTotalEnterosCremoso(informeDespachoResponse.getCantidadTotalEnterosCremoso() + dispatchDetailEntity.getCantidadEnteros());
              detalleInformeDespacho.setCantidadEnterosCremoso(detalleInformeDespacho.getCantidadEnterosCremoso() + dispatchDetailEntity.getCantidadEnteros());
            }
            if (dispatchDetailEntity.getCantidadMedios() != null) {
              informeDespachoResponse.setCantidadTotalMediosCremoso(informeDespachoResponse.getCantidadTotalMediosCremoso() + dispatchDetailEntity.getCantidadMedios());
              detalleInformeDespacho.setCantidadMediosCremoso(detalleInformeDespacho.getCantidadMediosCremoso() + dispatchDetailEntity.getCantidadMedios());
            }
            if (dispatchDetailEntity.getCantidadCuartos() != null) {
              informeDespachoResponse.setCantidadTotalCuartosCremoso(informeDespachoResponse.getCantidadTotalCuartosCremoso() + dispatchDetailEntity.getCantidadCuartos());
              detalleInformeDespacho.setCantidadCuartosCremoso(detalleInformeDespacho.getCantidadCuartosCremoso() + dispatchDetailEntity.getCantidadCuartos());
            }
          }
        }

      }


      informeDespachoResponse.getDetallesDespacho().add(detalleInformeDespacho);
    }

    informeDespachoResponse.setDetallesDespacho(detalleInformeDespachos);
    return informeDespachoResponse;
  }

  public DispatchResponse changeEstado(Long id) {
    Optional<DispatchEntity> despachoEntityOptional = despachoRepository.findById(id);
    if(despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }
    DispatchEntity dispatchEntity = despachoEntityOptional.get();


    VehicleEntity vehicleEntity = dispatchEntity.getVehiculo();
    UserEntity    userEntity    = dispatchEntity.getUsuario();

    if (dispatchEntity.getEstado().equals(DispatchStatus.PorEntregar.name())) {
      dispatchEntity.setEstado(DispatchStatus.Entregando.name());
    } else if (dispatchEntity.getEstado().equals(DispatchStatus.Entregando.name())) {
      dispatchEntity.setEstado(DispatchStatus.Despachado.name());
      vehicleEntity.setDisponible(true);
      userEntity.setIsDispatching(false);

      usuarioRepository.save(userEntity);
      vehiculoRepository.save(vehicleEntity);
    }


    return modelMapper.map(despachoRepository.save(dispatchEntity), DispatchResponse.class);
  }
}
