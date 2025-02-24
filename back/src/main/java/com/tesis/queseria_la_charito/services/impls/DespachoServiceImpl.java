package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.request.despacho.DespachoRequest;
import com.tesis.queseria_la_charito.dtos.request.despacho.DespachoUpdateRequest;
import com.tesis.queseria_la_charito.dtos.response.despacho.DespachoResponse;
import com.tesis.queseria_la_charito.dtos.response.despacho.DestinoResponse;
import com.tesis.queseria_la_charito.dtos.response.despacho.DetalleInformeDespacho;
import com.tesis.queseria_la_charito.dtos.response.despacho.InformeDespachoResponse;
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
import com.tesis.queseria_la_charito.services.DespachoService;
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
public class DespachoServiceImpl implements DespachoService {
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
  @Override
  public List<DespachoResponse> getByUser(String username) {
    List<DespachoResponse> despachoResponses = new ArrayList<>();

    Optional<UsuarioEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    Optional<DespachoEntity> despachoEntityOptional = despachoRepository.findByUsuarioAndEstadoNot(usuarioEntityOptional.get(), DespachoEstado.Despachado.name());
    if (despachoEntityOptional.isEmpty()) {
      return new ArrayList<>();
    }

    DespachoResponse despachoResponse = modelMapper.map(despachoEntityOptional.get(), DespachoResponse.class);
    despachoResponses.add(despachoResponse);

    return despachoResponses;
  }

  @Override
  public List<DespachoResponse> getAll(LocalDate fecha, Long destinoId) {
    Optional<DestinoEntity> destinoEntityOptional = destinoRepository.findById(destinoId);
    if (destinoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el destino");
    }

    List<DespachoEntity> lstDespachosEntities;
    List<DespachoResponse> lstDespachoResponse = new ArrayList<>();
    if(fecha != null) {
      lstDespachosEntities = despachoRepository.findByDestinoAndFecha(destinoEntityOptional.get(), fecha);
    } else {
      lstDespachosEntities = despachoRepository.findByDestino(destinoEntityOptional.get());
    }
    if (lstDespachosEntities.isEmpty()) {
      return new ArrayList<>();
    }

    lstDespachosEntities.forEach(entity -> lstDespachoResponse.add(modelMapper.map(entity, DespachoResponse.class)));

    return lstDespachoResponse;
  }

  @Override
  public DespachoResponse getById(Long id) {
    Optional<DespachoEntity> despachoEntityOptional = despachoRepository.findById(id);
    if (despachoEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró el despacho");
    }

    return modelMapper.map(despachoEntityOptional.get(), DespachoResponse.class);
  }

  @Override
  public DespachoResponse post(DespachoRequest despachoRequest) {
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
    return modelMapper.map(despachoRepository.save(despachoEntity), DespachoResponse.class);
  }

  @Override
  public DespachoResponse put(DespachoUpdateRequest despachoRequest, Long id) {
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

    return modelMapper.map(despachoRepository.save(despachoEntity), DespachoResponse.class);
  }

  @Override
  public DespachoResponse delete(Long id) {
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
      return modelMapper.map(despachoEntity, DespachoResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InformeDespachoResponse generateInforme(LocalDate fechaInicio, LocalDate fechaFin) {
    InformeDespachoResponse informeDespachoResponse = new InformeDespachoResponse();
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


    List<DetalleInformeDespacho> detalleInformeDespachos = new ArrayList<>();
    informeDespachoResponse.setDetallesDespacho(detalleInformeDespachos);
    for (DestinoEntity destinoEntity : destinoEntities) {
      DetalleInformeDespacho detalleInformeDespacho = new DetalleInformeDespacho();
      detalleInformeDespacho.setDestino(modelMapper.map(destinoEntity, DestinoResponse.class));
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

  @Override
  public DespachoResponse changeEstado(Long id) {
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


    return modelMapper.map(despachoRepository.save(despachoEntity), DespachoResponse.class);
  }
}
