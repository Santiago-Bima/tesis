package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.request.ElaboracionRequest;
import com.tesis.queseria_la_charito.dtos.request.procesosElaboracion.ControlCalidadRequest;
import com.tesis.queseria_la_charito.dtos.request.procesosElaboracion.DetalleCorteRequest;
import com.tesis.queseria_la_charito.dtos.request.procesosElaboracion.MaduracionRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.DetalleInsumoInformeElaboracion;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.ElaboracionResponse;
import com.tesis.queseria_la_charito.dtos.response.lote.LoteResponse;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.InformeElaboracionResponse;
import com.tesis.queseria_la_charito.entities.*;
import com.tesis.queseria_la_charito.entities.formula.DetalleFormulaEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import com.tesis.queseria_la_charito.entities.LoteEntity;
import com.tesis.queseria_la_charito.entities.procesosElaboracion.ControlCalidadEntity;
import com.tesis.queseria_la_charito.entities.procesosElaboracion.DetalleCorteEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import com.tesis.queseria_la_charito.models.Estado;
import com.tesis.queseria_la_charito.models.Quesos;
import com.tesis.queseria_la_charito.models.TipoCorte;
import com.tesis.queseria_la_charito.repositories.ElaboracionRepository;
import com.tesis.queseria_la_charito.repositories.formula.FormulaRepository;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.LoteRepository;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;
import com.tesis.queseria_la_charito.services.ElaboracionService;
import com.tesis.queseria_la_charito.services.InsumoService;
import com.tesis.queseria_la_charito.services.LoteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class ElaboracionesServiceImpl implements ElaboracionService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ElaboracionRepository elaboracionRepository;

  @Autowired
  private FormulaRepository formulaRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private LoteService loteService;

  @Autowired
  private UsuarioRepository usuarioRepository;

  @Autowired
  private LoteRepository loteRepository;

  @Autowired
  private InsumoService insumoService;


  @Override
  public List<ElaboracionResponse> getAll(String username, LocalDate fechaInicio, LocalDate fechaFin, Long productId) {
    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(productId);
    if(itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el producto");
    }

    Optional<UsuarioEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    List<ElaboracionEntity> listaElaboraciones;
    if(fechaInicio == null || fechaFin == null){
      listaElaboraciones = elaboracionRepository.findByUsuarioAndFormulaTipoQuesoItem(usuarioEntityOptional.get(),
          itemEntityOptional.get());
    } else {
      listaElaboraciones = elaboracionRepository.findByUsuarioAndFormulaTipoQuesoItemAndFechaBetween(usuarioEntityOptional.get(), itemEntityOptional.get(), fechaInicio, fechaFin);
    }

    if(listaElaboraciones.isEmpty()){
      return new ArrayList<>();
    }

    List<ElaboracionResponse> elaboracionResponses = new ArrayList<>();
    for (int i = 0; i < listaElaboraciones.size(); i++) {
      elaboracionResponses.add(modelMapper.map(listaElaboraciones.get(i), ElaboracionResponse.class));
    }

    return elaboracionResponses;
  }

  @Override
  public ElaboracionResponse getById(String username, String id) {
    Optional<UsuarioEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findByUsuarioAndId(usuarioEntityOptional.get(), id);
    if(elaboracionEntityOptional.isEmpty()){
      throw new EntityNotFoundException("No se encontró una elaboración con ese id");
    }

    return modelMapper.map(elaboracionEntityOptional.get(), ElaboracionResponse.class);
  }

  @Override
  public ElaboracionResponse post(ElaboracionRequest elaboracionRequest) {
    ElaboracionEntity elaboracionEntity = new ElaboracionEntity();
    Optional<UsuarioEntity> usuarioEntityOptional = usuarioRepository.findByUsername(elaboracionRequest.getUsuario());
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    elaboracionEntity.setUsuario(usuarioEntityOptional.get());

    Optional<FormulaEntity> formulaEntityOptional = formulaRepository.findById(elaboracionRequest.getIdFormula());
    if(formulaEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado la fórmula");
    }

    FormulaEntity formulaEntity = formulaEntityOptional.get();

    formulaEntity.getDetallesFormulas().forEach(detalle -> {
      ItemEntity insumo = detalle.getInsumo();
      int relacionLeche = elaboracionRequest.getCantidadLeche() / formulaEntity.getCantidadLeche();
      AtomicReference<Integer> cantidad     = new AtomicReference<>(detalle.getCantidad() * relacionLeche);
      List<LoteEntity>         loteEntities = loteRepository.findByItemAndEstadoAndMostrar(insumo, Estado.Disponible.name(), true);
      if (loteEntities.isEmpty()) {
        throw new RuntimeException("El insumo " + insumo.getNombre() + " no posee lotes");
      }
      int contador = 0;

      while (cantidad.get() != 0) {
        LoteEntity lote = loteEntities.get(contador);

        int diferencia = lote.getUnidades() - cantidad.get();
        lote.setUnidades(Math.max(diferencia, 0));
        cantidad.set(diferencia > 0 ? 0 : diferencia * -1);

        if (lote.getUnidades() == 0) {
          lote.setEstado(Estado.SinStock.name());

          try {
            loteRepository.save(lote);
          } catch (Exception e) {
            throw new RuntimeException("Hubo un error al eliminar el stock de insumos utilizados en la elaboración: " + e);
          }
        }

        contador ++;
      }
    });

    elaboracionEntity.setFormula(formulaEntity);

    String inicialItem = formulaEntity.getTipoQueso().getItem().getNombre().substring(0, 1).toUpperCase();
    String cantidadElaboraciones = String.valueOf(elaboracionRepository.findAll().size());
    elaboracionEntity.setId("Q" + inicialItem + elaboracionRequest.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + cantidadElaboraciones);

    elaboracionEntity.setFecha(elaboracionRequest.getFecha());
    elaboracionEntity.setCantidadLeche(elaboracionRequest.getCantidadLeche());
    elaboracionEntity.setTiempoSalado(elaboracionRequest.getTiempoSalado());

    LoteResponse loteResponse = loteService.postLote(formulaEntity.getTipoQueso().getItem().getId(), 0);

    elaboracionEntity.setLote(modelMapper.map(loteResponse, LoteEntity.class));

    return modelMapper.map(elaboracionRepository.save(elaboracionEntity), ElaboracionResponse.class);
  }

  @Override
  public ElaboracionResponse updateCortes(DetalleCorteRequest detalleCorteRequest, String idElaboracion) throws Exception{
    Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    if (elaboracionEntityOptional.get().getLote().getUnidades() != 0 && elaboracionEntityOptional.get().getLote().getEstado().equals(Estado.Elaborando.name())) {
      throw new RuntimeException("La elaboración ya fué creada con sus cortes, no se pueden editar los cortes");
    }

    ElaboracionEntity elaboracionEntity = elaboracionEntityOptional.get();

    if(!elaboracionEntity.getFormula().getTipoQueso().getItem().getNombre().equals(Quesos.Cremoso.name()) && !detalleCorteRequest.getCorte().equals(TipoCorte.Entero.name())) {
      throw new Exception("El queso para el que está pensada la elaboración no permite más de 1 corte y el mismo debe ser de tipo Entero");
    }

    DetalleCorteEntity detalleCorteEntity = modelMapper.map(detalleCorteRequest, DetalleCorteEntity.class);
    detalleCorteEntity.setElaboracion(elaboracionEntity);
    elaboracionEntity.getLote().setUnidades(elaboracionEntity.getLote().getUnidades() + detalleCorteEntity.getCantidad());

    elaboracionEntity.setDetalleCorte(detalleCorteEntity);
    return modelMapper.map(elaboracionRepository.save(elaboracionEntity), ElaboracionResponse.class);
  }

  @Override
  public ElaboracionResponse updateEmbolsado(LocalDate fechaEmbolsado, String idElaboracion) throws Exception {
    Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ElaboracionEntity elaboracionEntity = elaboracionEntityOptional.get();

    if (fechaEmbolsado.isBefore(elaboracionEntity.getFecha())) {
      throw new Exception("La fecha de embolsado debe ser posterior al inicio de la elaboración");
    } else if (elaboracionEntity.getFechaSalidaMaduracion() == null || fechaEmbolsado.isBefore(elaboracionEntity.getFechaSalidaMaduracion())) {
      throw new Exception("La fecha de embolsado debe ser posterior a la maduración");
    }

    if(elaboracionEntity.getFormula().getTipoQueso().getItem().getNombre().equals(Quesos.Pategras.name())) {
      throw new Exception("El queso para el que está pensada la elaboración no permite tipos de cortes");
    }

    elaboracionEntity.setFechaEmbolsado(fechaEmbolsado);

    return modelMapper.map(elaboracionRepository.save(elaboracionEntity), ElaboracionResponse.class);
  }

  @Override
  public ElaboracionResponse updateMaduracion(MaduracionRequest maduracionRequest, String idElaboracion) throws Exception {
    Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ElaboracionEntity elaboracionEntity = elaboracionEntityOptional.get();
    LocalDate fechaEntrada = maduracionRequest.getFechaEntrada();
    LocalDate fechaSalida = maduracionRequest.getFechaSalida();
    Integer diasMaduracion = elaboracionEntity.getFormula().getTipoQueso().getDiasMaduracion();

    if (fechaEntrada.isBefore(elaboracionEntity.getFecha())) {
      throw new Exception("La fecha de entrada debe ser posterior al inicio de la elaboración");
    }

    if (fechaSalida.isBefore(fechaEntrada.plusDays(diasMaduracion))) {
      throw new Exception("La fecha de salida debe ser al menos " + diasMaduracion.toString() + " días después de la fecha de entrada.");
    }

    elaboracionEntity.setFechaEntradaMaduracion(fechaEntrada);
    elaboracionEntity.setFechaSalidaMaduracion(fechaSalida);
    return modelMapper.map(elaboracionRepository.save(elaboracionEntity), ElaboracionResponse.class);
  }

  @Override
  public ElaboracionResponse updatePintado(LocalDate fechaPintado, String idElaboracion) throws Exception {
    Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ElaboracionEntity elaboracionEntity = elaboracionEntityOptional.get();

    if (fechaPintado.isBefore(elaboracionEntity.getFecha())) {
      throw new Exception("La fecha de pintado debe ser posterior al inicio de la elaboración");
    } else if (elaboracionEntity.getFechaSalidaMaduracion() == null || fechaPintado.isBefore(elaboracionEntity.getFechaSalidaMaduracion())) {
      throw new Exception("La fecha de pintado debe ser posterior a la maduración");
    }

    if(!elaboracionEntity.getFormula().getTipoQueso().getItem().getNombre().equals(Quesos.Pategras.name())) {
      throw new Exception("El queso para el que está pensada la elaboración no permite tipos de cortes");
    }

    elaboracionEntity.setFechaPintado(fechaPintado);

    return modelMapper.map(elaboracionRepository.save(elaboracionEntity), ElaboracionResponse.class);
  }

  @Override
  public ElaboracionResponse updateControl(ControlCalidadRequest controlCalidadRequest, String idElaboracion) throws Exception {
    Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ElaboracionEntity elaboracionEntity = elaboracionEntityOptional.get();
    LocalDate fechaControl = controlCalidadRequest.getFecha();

    if (fechaControl.isBefore(elaboracionEntity.getFecha())) {
      throw new Exception("La fecha de control de calidad debe ser posterior al inicio de la elaboración.");
    }

    LocalDate fechaEmbolsado = elaboracionEntity.getFechaEmbolsado();
    LocalDate fechaPintado = elaboracionEntity.getFechaPintado();

    if (fechaEmbolsado != null && fechaControl.isBefore(fechaEmbolsado)) {
      throw new Exception("La fecha de control de calidad debe ser posterior al embolsado.");
    }

    if (fechaPintado != null && fechaControl.isBefore(fechaPintado)) {
      throw new Exception("La fecha de control de calidad debe ser posterior al pintado.");
    }

    ControlCalidadEntity controlCalidadEntity = modelMapper.map(controlCalidadRequest ,ControlCalidadEntity.class);
    controlCalidadEntity.setElaboracion(elaboracionEntity);
    elaboracionEntity.setControlCalidad(controlCalidadEntity);
    elaboracionEntity.getLote().setEstado(Estado.Terminado.name());
    return modelMapper.map(elaboracionRepository.save(elaboracionEntity), ElaboracionResponse.class);
  }

  @Override
  public ElaboracionResponse deleteElaboracion(String id) {
    Optional<ElaboracionEntity> elaboracionEntityOptional = elaboracionRepository.findById(id);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración para eliminar");
    }

    ElaboracionEntity elaboracionEntity = elaboracionEntityOptional.get();

    if (elaboracionEntity.getLote().getUnidades() > 0) {
      throw new IllegalStateException("No se puede eliminar el item porque tiene lotes existentes.");
    }

    try{
      elaboracionRepository.delete(elaboracionEntity);
      return modelMapper.map(elaboracionEntity, ElaboracionResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public InformeElaboracionResponse generateInforme(LocalDate fechaInicio, LocalDate fechaFin) {
    List<ElaboracionEntity> elaboracionEntities = elaboracionRepository.findByFechaBetween(fechaInicio, fechaFin);
    if (elaboracionEntities.isEmpty()) {
      elaboracionEntities = new ArrayList<>();
    }

    InformeElaboracionResponse informe = new InformeElaboracionResponse();
    informe.setCantidadElaboraciones(0);
    informe.setCantidadIncompletas(0);
    informe.setCantidadPategras(0);
    informe.setCantidadBarra(0);
    informe.setCantidadEnterosCremoso(0);
    informe.setCantidadMediosCremoso(0);
    informe.setCantidadCuartosCremoso(0);

    List<DetalleInsumoInformeElaboracion> detallesInsumos = new ArrayList<>();

    List<ItemResponse> listaInsumos = insumoService.getItems();

    for (ItemResponse item : listaInsumos) {
      DetalleInsumoInformeElaboracion detalleInsumo = new DetalleInsumoInformeElaboracion();
      detalleInsumo.setInsumo(item);
      detalleInsumo.setTotal(0);

      detallesInsumos.add(detalleInsumo);
    }

    for(ElaboracionEntity elaboracion : elaboracionEntities) {
      for (DetalleFormulaEntity detalleFormula : elaboracion.getFormula().getDetallesFormulas()) {
        for (DetalleInsumoInformeElaboracion detalleInsumo : detallesInsumos) {
          if (detalleFormula.getInsumo().getNombre().equals(detalleInsumo.getInsumo().getNombreItem())) {
            detalleInsumo.setTotal(detalleInsumo.getTotal() + ((elaboracion.getCantidadLeche() / elaboracion.getFormula().getCantidadLeche()) * detalleFormula.getCantidad()));
          }
        }
      }

      informe.setCantidadElaboraciones(informe.getCantidadElaboraciones() + 1);

      if (elaboracion.getDetalleCorte() == null) {
        informe.setCantidadIncompletas(informe.getCantidadIncompletas() + 1);
      } else {
        if (elaboracion.getDetalleCorte().getCorte().equals(TipoCorte.Entero.name())) {
          if (elaboracion.getFormula().getTipoQueso().getItem().getNombre().equals(Quesos.Pategras.name())) {
            informe.setCantidadPategras(informe.getCantidadPategras() + elaboracion.getDetalleCorte().getCantidad());
          } else if (elaboracion.getFormula().getTipoQueso().getItem().getNombre().equals(Quesos.Barra.name())) {
            informe.setCantidadBarra(informe.getCantidadBarra() + elaboracion.getDetalleCorte().getCantidad());
          } else {
            informe.setCantidadEnterosCremoso(informe.getCantidadEnterosCremoso() + elaboracion.getDetalleCorte().getCantidad());
          }
        } else if (elaboracion.getDetalleCorte().getCorte().equals(TipoCorte.Medio.name())) {
          informe.setCantidadMediosCremoso(informe.getCantidadMediosCremoso() + elaboracion.getDetalleCorte().getCantidad());
        } else {
          informe.setCantidadCuartosCremoso(informe.getCantidadCuartosCremoso() + elaboracion.getDetalleCorte().getCantidad());
        }
      }
    }

    informe.setInsumosUtilizados(detallesInsumos);

    return informe;
  }
}
