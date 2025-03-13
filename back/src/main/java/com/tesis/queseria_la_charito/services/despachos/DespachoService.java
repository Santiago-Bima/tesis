package com.tesis.queseria_la_charito.services.despachos;

import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchRequest;
import com.tesis.queseria_la_charito.dtos.request.dispatch.DispatchUpdateRequest;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DispatchResponse;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DestinationResponse;
import com.tesis.queseria_la_charito.dtos.response.dispatch.ReportDetailDespacho;
import com.tesis.queseria_la_charito.dtos.response.dispatch.DispatchReportResponse;
import com.tesis.queseria_la_charito.entities.*;
import com.tesis.queseria_la_charito.entities.despacho.DespachoEntity;
import com.tesis.queseria_la_charito.entities.despacho.DestinoEntity;
import com.tesis.queseria_la_charito.entities.despacho.DetalleDespachoEntity;
import com.tesis.queseria_la_charito.entities.despacho.VehiculoEntity;
import com.tesis.queseria_la_charito.entities.LoteEntity;
import com.tesis.queseria_la_charito.entities.procesosElaboracion.DetalleCorteEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import com.tesis.queseria_la_charito.models.DespachoEstado;
import com.tesis.queseria_la_charito.models.Estado;
import com.tesis.queseria_la_charito.models.Quesos;
import com.tesis.queseria_la_charito.models.TipoCorte;
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

    Optional<UsuarioEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    Optional<DespachoEntity> despachoEntityOptional = despachoRepository.findByUsuarioAndEstadoNot(usuarioEntityOptional.get(), DespachoEstado.Despachado.name());
    if (despachoEntityOptional.isEmpty()) {
      return new ArrayList<>();
    }

    DispatchResponse despachoResponse = modelMapper.map(despachoEntityOptional.get(), DispatchResponse.class);
    despachoResponses.add(despachoResponse);

    return despachoResponses;
  }

  public List<DispatchResponse> getAll(LocalDate fecha, Long destinoId) {
    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findById(destinoId);
    if (destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }

    List<DespachoEntity>   lstDespachosEntities;
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
    Optional<DespachoEntity> despachoEntityOptional = despachoRepository.findById(id);
    if (despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el despacho");
    }

    return modelMapper.map(despachoEntityOptional.get(), DispatchResponse.class);
  }

  public DispatchResponse post(DispatchRequest despachoRequest) {
    DespachoEntity despachoEntity = new DespachoEntity();
    despachoEntity.setLstDetallesDespacho(new ArrayList<>());
    despachoEntity.setFecha(despachoRequest.getFecha());
    despachoEntity.setQueso(despachoRequest.getQueso());
    despachoEntity.setEstado(DespachoEstado.PorEntregar.name());

    Optional<UsuarioEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(despachoRequest.getUsuario(), true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    UsuarioEntity usuarioEntity = usuarioEntityOptional.get();

    usuarioEntity.setIsDispatching(true);
    despachoEntity.setUsuario(usuarioEntity);

    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findById(despachoRequest.getDestino());
    if (destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el destino");
    }
    despachoEntity.setDestino(destinoEntityOptional.get());

    Optional<VehiculoEntity> vehiculoEntityOptional = vehiculoRepository.findById(despachoRequest.getVehiculo());
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el vehículo");
    }
    VehiculoEntity vehiculoEntity = vehiculoEntityOptional.get();
    if (!vehiculoEntity.getDisponible()) {
      throw new RuntimeException("El vehículo ya se encuentra en uso");
    }
    vehiculoEntity.setDisponible(false);
    despachoEntity.setVehiculo(vehiculoEntity);

    Optional<ItemEntity> tipoQuesoOptional = itemRepository.findByNombre(despachoRequest.getQueso());
    if (tipoQuesoOptional.isEmpty()) {
      throw new EntityNotFoundException("No existe ese tipo de queso");
    }

    ItemEntity       tipoQueso = tipoQuesoOptional.get();
    List<LoteEntity> lotes     = loteRepository.findByItemAndEstadoAndMostrar(tipoQueso, Estado.Terminado.name(), true);
    if (lotes.isEmpty()) {
      throw new RuntimeException("No hay ningún lote para despachar");
    }
    Integer totalEntero = despachoRequest.getTotalEnteros();
    Integer totalMedio = despachoRequest.getTotalMedios();
    Integer totalCuartos = despachoRequest.getTotalCuartos();
    int contador = 0;

    despachoEntity.setCantidadTotal(totalEntero + totalMedio + totalCuartos);

    while (totalEntero > 0 || totalMedio > 0 || totalCuartos > 0) {
      if (lotes.size() - 1 < contador) {
        throw new RuntimeException("No hay suficientes lotes para despachar la cantidad de quesos requeridos");
      }
      DetalleDespachoEntity detalleDespachoEntity = new DetalleDespachoEntity();
      LoteEntity lote = lotes.get(contador);
      boolean modificado = false;

      Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findByLote(lote);
      if (elaboracionEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se encontró la elaboración del lote");
      }
      ElaboracionEntity elaboracion = elaboracionEntityOptional.get();

      AtomicInteger enterosLote = new AtomicInteger(0);
      AtomicInteger mediosLote = new AtomicInteger(0);
      AtomicInteger cuartosLote = new AtomicInteger(0);

      DetalleCorteEntity corte = elaboracion.getDetalleCorte();
      if (corte.getCorte().equals(TipoCorte.Entero.name())) {
        enterosLote.addAndGet(lote.getUnidades());
      } else if (corte.getCorte().equals(TipoCorte.Medio.name())) {
        mediosLote.addAndGet(lote.getUnidades());
      } else if (corte.getCorte().equals(TipoCorte.Cuarto.name())) {
        cuartosLote.addAndGet(lote.getUnidades());
      }

      if (enterosLote.get() > 0) {
        int diferencia = totalEntero - enterosLote.get();
        detalleDespachoEntity.setCantidadEnteros(diferencia > 0 ? enterosLote.get() : totalEntero);
        lote.setUnidades(lote.getUnidades() - (diferencia > 0 ? enterosLote.get() : totalEntero));
        enterosLote.set(diferencia >= 0 ? 0 : enterosLote.get() - diferencia);
        totalEntero = Math.max(diferencia, 0);
        modificado = true;
      }

      if (mediosLote.get() > 0) {
        int diferencia = totalMedio - mediosLote.get();
        detalleDespachoEntity.setCantidadMedios(diferencia > 0 ? mediosLote.get() : totalMedio);
        lote.setUnidades(lote.getUnidades() - (diferencia > 0 ? mediosLote.get() : totalMedio));
        totalMedio = Math.max(diferencia, 0);
        mediosLote.set(diferencia >= 0 ? 0 : mediosLote.get() - diferencia);
        modificado = true;
      }

      if (cuartosLote.get() > 0) {
        int diferencia = totalCuartos - cuartosLote.get();
        detalleDespachoEntity.setCantidadCuartos(diferencia > 0 ? cuartosLote.get() : totalCuartos);
        lote.setUnidades(lote.getUnidades() - (diferencia > 0 ? cuartosLote.get() : totalCuartos));
        totalCuartos = Math.max(diferencia, 0);
        cuartosLote.set(diferencia >= 0 ? 0 : cuartosLote.get() - diferencia);
        modificado = true;
      }

      if (modificado) {
        if (enterosLote.get() == 0 && mediosLote.get() == 0 && cuartosLote.get() == 0) {
          lote.setEstado(Estado.Despachado.name());
        }

        detalleDespachoEntity.setLote(lote);
        detalleDespachoEntity.setDespacho(despachoEntity);
        despachoEntity.getLstDetallesDespacho().add(detalleDespachoEntity);
      }
      contador ++;
    }

    vehiculoRepository.save(vehiculoEntity);
    usuarioRepository.save(usuarioEntity);
    return modelMapper.map(despachoRepository.save(despachoEntity), DispatchResponse.class);
  }

  public DispatchResponse put(DispatchUpdateRequest despachoRequest, Long id) {
    Optional<DespachoEntity> despachoEntityOptional = despachoRepository.findById(id);
    if(despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }
    DespachoEntity despachoEntity = despachoEntityOptional.get();

    if (!despachoEntity.getEstado().equals(DespachoEstado.PorEntregar.name())) {
      throw new RuntimeException("No se puede modificar un despacho que está entregado o en proceso");
    }

    Optional<VehiculoEntity> vehiculoEntityOptional = vehiculoRepository.findById(despachoRequest.getIdVehiculo());
    if (vehiculoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el vehículo");
    }
    VehiculoEntity vehiculoEntity = vehiculoEntityOptional.get();
    if (!vehiculoEntity.getDisponible()) {
      throw new RuntimeException("El vehículo ya se encuentra en uso");
    }
    despachoEntity.setVehiculo(vehiculoEntityOptional.get());

    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findById(despachoRequest.getIdDestino());
    if (destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }
    despachoEntity.setDestino(destinoEntityOptional.get());

    return modelMapper.map(despachoRepository.save(despachoEntity), DispatchResponse.class);
  }

  public DispatchResponse delete(Long id) {
    Optional<DespachoEntity> despachoEntityOptional = despachoRepository.findById(id);
    if (despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }

    DespachoEntity despachoEntity = despachoEntityOptional.get();

    if (despachoEntity.getEstado().equals(DespachoEstado.Entregando.name())) {
      throw new RuntimeException("No se puede eliminar un despacho que está siendo entregado");
    }
    if (despachoEntity.getEstado().equals(DespachoEstado.PorEntregar.name())) {
      despachoEntity.getUsuario().setIsDispatching(false);
      usuarioRepository.save(despachoEntity.getUsuario());
      despachoEntity.getVehiculo().setDisponible(true);
      vehiculoRepository.save(despachoEntity.getVehiculo());
    }

    if (!despachoEntity.getLstDetallesDespacho().isEmpty()) {
      despachoEntity.getLstDetallesDespacho().forEach(detalle -> {
        Optional<LoteEntity> loteEntityOptional = loteRepository.findById(detalle.getLote().getId());
        if (loteEntityOptional.isEmpty()) {
          throw new EntityNotFoundException("No se ha encontrado el lote del detalle");
        }

        if (despachoEntity.getEstado().equals(DespachoEstado.PorEntregar.name())) {
          if (loteEntityOptional.get().getElaboracion().getDetalleCorte().getCorte().equals(TipoCorte.Entero.name())) {
            loteEntityOptional.get().setUnidades(loteEntityOptional.get().getUnidades() + detalle.getCantidadEnteros());
          } else  if (loteEntityOptional.get().getElaboracion().getDetalleCorte().getCorte().equals(TipoCorte.Medio.name())) {
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
      despachoRepository.delete(despachoEntity);
      return modelMapper.map(despachoEntity, DispatchResponse.class);
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

    List<DestinoEntity> destinoEntities = destinoRepository.findAll();
    if (destinoEntities.isEmpty()) {
      destinoEntities = new ArrayList<>();
    }


    List<ReportDetailDespacho> detalleInformeDespachos = new ArrayList<>();
    informeDespachoResponse.setDetallesDespacho(detalleInformeDespachos);
    for (DestinoEntity destinoEntity : destinoEntities) {
      ReportDetailDespacho detalleInformeDespacho = new ReportDetailDespacho();
      detalleInformeDespacho.setDestino(modelMapper.map(destinoEntity, DestinationResponse.class));
      detalleInformeDespacho.setCantidadPategras(0);
      detalleInformeDespacho.setCantidadBarra(0);
      detalleInformeDespacho.setCantidadMediosCremoso(0);
      detalleInformeDespacho.setCantidadCuartosCremoso(0);
      detalleInformeDespacho.setCantidadEnterosCremoso(0);

      List<DespachoEntity> despachoEntities = despachoRepository.findByDestinoAndFechaBetween(destinoEntity, fechaInicio, fechaFin);
      for (DespachoEntity despachoEntity : despachoEntities) {
        informeDespachoResponse.setCantidadDespachos(informeDespachoResponse.getCantidadDespachos() + 1);
        informeDespachoResponse.setTotalUnidadesDespachadas(informeDespachoResponse.getTotalUnidadesDespachadas() + despachoEntity.getCantidadTotal());


        if (despachoEntity.getQueso().equals(Quesos.Pategras.name())) {
          for (DetalleDespachoEntity detalleDespachoEntity : despachoEntity.getLstDetallesDespacho()) {
            informeDespachoResponse.setCantidadTotalPategras(informeDespachoResponse.getCantidadTotalPategras() + detalleDespachoEntity.getCantidadEnteros());
            detalleInformeDespacho.setCantidadPategras(detalleInformeDespacho.getCantidadPategras() + detalleDespachoEntity.getCantidadEnteros());
          }
        } else if (despachoEntity.getQueso().equals(Quesos.Barra.name())) {
          for (DetalleDespachoEntity detalleDespachoEntity : despachoEntity.getLstDetallesDespacho()) {
            informeDespachoResponse.setCantidadTotalBarra(informeDespachoResponse.getCantidadTotalBarra() + detalleDespachoEntity.getCantidadEnteros());
            detalleInformeDespacho.setCantidadBarra(detalleInformeDespacho.getCantidadBarra() + detalleDespachoEntity.getCantidadEnteros());
          }
        } else {
          for (DetalleDespachoEntity detalleDespachoEntity : despachoEntity.getLstDetallesDespacho()) {
            if (detalleDespachoEntity.getCantidadEnteros() != null) {
              informeDespachoResponse.setCantidadTotalEnterosCremoso(informeDespachoResponse.getCantidadTotalEnterosCremoso() + detalleDespachoEntity.getCantidadEnteros());
              detalleInformeDespacho.setCantidadEnterosCremoso(detalleInformeDespacho.getCantidadEnterosCremoso() + detalleDespachoEntity.getCantidadEnteros());
            }
            if (detalleDespachoEntity.getCantidadMedios() != null) {
              informeDespachoResponse.setCantidadTotalMediosCremoso(informeDespachoResponse.getCantidadTotalMediosCremoso() + detalleDespachoEntity.getCantidadMedios());
              detalleInformeDespacho.setCantidadMediosCremoso(detalleInformeDespacho.getCantidadMediosCremoso() + detalleDespachoEntity.getCantidadMedios());
            }
            if (detalleDespachoEntity.getCantidadCuartos() != null) {
              informeDespachoResponse.setCantidadTotalCuartosCremoso(informeDespachoResponse.getCantidadTotalCuartosCremoso() + detalleDespachoEntity.getCantidadCuartos());
              detalleInformeDespacho.setCantidadCuartosCremoso(detalleInformeDespacho.getCantidadCuartosCremoso() + detalleDespachoEntity.getCantidadCuartos());
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
    Optional<DespachoEntity> despachoEntityOptional = despachoRepository.findById(id);
    if(despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el despacho");
    }
    DespachoEntity despachoEntity = despachoEntityOptional.get();


    VehiculoEntity vehiculoEntity = despachoEntity.getVehiculo();
    UsuarioEntity usuarioEntity = despachoEntity.getUsuario();

    if (despachoEntity.getEstado().equals(DespachoEstado.PorEntregar.name())) {
      despachoEntity.setEstado(DespachoEstado.Entregando.name());
    } else if (despachoEntity.getEstado().equals(DespachoEstado.Entregando.name())) {
      despachoEntity.setEstado(DespachoEstado.Despachado.name());
      vehiculoEntity.setDisponible(true);
      usuarioEntity.setIsDispatching(false);

      usuarioRepository.save(usuarioEntity);
      vehiculoRepository.save(vehiculoEntity);
    }


    return modelMapper.map(despachoRepository.save(despachoEntity), DispatchResponse.class);
  }
}
