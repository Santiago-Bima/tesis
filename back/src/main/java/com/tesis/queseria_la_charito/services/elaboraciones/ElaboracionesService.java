package com.tesis.queseria_la_charito.services.elaboraciones;

import com.tesis.queseria_la_charito.dtos.request.production.ProductionRequest;
import com.tesis.queseria_la_charito.dtos.request.production.QualityControlRequest;
import com.tesis.queseria_la_charito.dtos.request.production.CutDetailRequest;
import com.tesis.queseria_la_charito.dtos.request.production.MadurationRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import com.tesis.queseria_la_charito.dtos.response.production.SuppliesReportDetailElaboracion;
import com.tesis.queseria_la_charito.dtos.response.production.ProductionResponse;
import com.tesis.queseria_la_charito.dtos.response.batch.BatchResponse;
import com.tesis.queseria_la_charito.dtos.response.production.ProductionReportResponse;
import com.tesis.queseria_la_charito.entities.*;
import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaDetailEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import com.tesis.queseria_la_charito.entities.production.ProductionEntity;
import com.tesis.queseria_la_charito.entities.production.processes.QualityControlEntity;
import com.tesis.queseria_la_charito.entities.production.processes.CutDetailEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import com.tesis.queseria_la_charito.models.Status;
import com.tesis.queseria_la_charito.models.Cheese;
import com.tesis.queseria_la_charito.models.CutType;
import com.tesis.queseria_la_charito.repositories.ElaboracionRepository;
import com.tesis.queseria_la_charito.repositories.formula.FormulaRepository;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.LoteRepository;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;
import com.tesis.queseria_la_charito.services.formulas.InsumosService;
import com.tesis.queseria_la_charito.services.lotes.LoteService;
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
public class ElaboracionesService {
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
  private InsumosService insumoService;


  public List<ProductionResponse> getAll(String username, LocalDate fechaInicio, LocalDate fechaFin, Long productId) {
    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(productId);
    if(itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el producto");
    }

    Optional<UserEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    List<ProductionEntity> listaElaboraciones;
    if(fechaInicio == null || fechaFin == null){
      listaElaboraciones = elaboracionRepository.findByUsuarioAndFormulaTipoQuesoItem(usuarioEntityOptional.get(),
          itemEntityOptional.get());
    } else {
      listaElaboraciones = elaboracionRepository.findByUsuarioAndFormulaTipoQuesoItemAndFechaBetween(usuarioEntityOptional.get(), itemEntityOptional.get(), fechaInicio, fechaFin);
    }

    if(listaElaboraciones.isEmpty()){
      return new ArrayList<>();
    }

    List<ProductionResponse> elaboracionResponses = new ArrayList<>();
    for (int i = 0; i < listaElaboraciones.size(); i++) {
      elaboracionResponses.add(modelMapper.map(listaElaboraciones.get(i), ProductionResponse.class));
    }

    return elaboracionResponses;
  }

  public ProductionResponse getById(String username, String id) {
    Optional<UserEntity> usuarioEntityOptional = usuarioRepository.findByUsernameAndMostrar(username, true);
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findByUsuarioAndId(usuarioEntityOptional.get(), id);
    if(elaboracionEntityOptional.isEmpty()){
      throw new EntityNotFoundException("No se encontró una elaboración con ese id");
    }

    return modelMapper.map(elaboracionEntityOptional.get(), ProductionResponse.class);
  }

  public ProductionResponse post(ProductionRequest elaboracionRequest) {
    ProductionEntity     productionEntity      = new ProductionEntity();
    Optional<UserEntity> usuarioEntityOptional = usuarioRepository.findByUsername(elaboracionRequest.getUsuario());
    if (usuarioEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el usuario");
    }

    productionEntity.setUsuario(usuarioEntityOptional.get());

    Optional<FormulaEntity> formulaEntityOptional = formulaRepository.findById(elaboracionRequest.getIdFormula());
    if(formulaEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado la fórmula");
    }

    FormulaEntity formulaEntity = formulaEntityOptional.get();

    formulaEntity.getDetallesFormulas().forEach(detalle -> {
      ItemEntity insumo = detalle.getInsumo();
      int relacionLeche = elaboracionRequest.getCantidadLeche() / formulaEntity.getCantidadLeche();
      AtomicReference<Integer> cantidad     = new AtomicReference<>(detalle.getCantidad() * relacionLeche);
      List<BatchEntity>        loteEntities = loteRepository.findByItemAndEstadoAndMostrar(insumo, Status.Disponible.name(), true);
      if (loteEntities.isEmpty()) {
        throw new RuntimeException("El insumo " + insumo.getNombre() + " no posee lotes");
      }
      int contador = 0;

      while (cantidad.get() != 0) {
        BatchEntity lote = loteEntities.get(contador);

        int diferencia = lote.getUnidades() - cantidad.get();
        lote.setUnidades(Math.max(diferencia, 0));
        cantidad.set(diferencia > 0 ? 0 : diferencia * -1);

        if (lote.getUnidades() == 0) {
          lote.setEstado(Status.SinStock.name());

          try {
            loteRepository.save(lote);
          } catch (Exception e) {
            throw new RuntimeException("Hubo un error al eliminar el stock de insumos utilizados en la elaboración: " + e);
          }
        }

        contador ++;
      }
    });

    productionEntity.setFormula(formulaEntity);

    String inicialItem = formulaEntity.getTipoQueso().getItem().getNombre().substring(0, 1).toUpperCase();
    String cantidadElaboraciones = String.valueOf(elaboracionRepository.findAll().size());
    productionEntity.setId("Q" + inicialItem + elaboracionRequest.getFecha().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + cantidadElaboraciones);

    productionEntity.setFecha(elaboracionRequest.getFecha());
    productionEntity.setCantidadLeche(elaboracionRequest.getCantidadLeche());
    productionEntity.setTiempoSalado(elaboracionRequest.getTiempoSalado());

    BatchResponse loteResponse = loteService.postLote(formulaEntity.getTipoQueso().getItem().getId(), 0);

    productionEntity.setLote(modelMapper.map(loteResponse, BatchEntity.class));

    return modelMapper.map(elaboracionRepository.save(productionEntity), ProductionResponse.class);
  }

  public ProductionResponse updateCortes(CutDetailRequest detalleCorteRequest, String idElaboracion) throws Exception{
    Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    if (elaboracionEntityOptional.get().getLote().getUnidades() != 0 && elaboracionEntityOptional.get().getLote().getEstado().equals(Status.Elaborando.name())) {
      throw new RuntimeException("La elaboración ya fué creada con sus cortes, no se pueden editar los cortes");
    }

    ProductionEntity productionEntity = elaboracionEntityOptional.get();

    if(!productionEntity.getFormula().getTipoQueso().getItem().getNombre().equals(Cheese.Cremoso.name()) && !detalleCorteRequest.getCorte().equals(CutType.Whole.name())) {
      throw new Exception("El queso para el que está pensada la elaboración no permite más de 1 corte y el mismo debe ser de tipo Entero");
    }

    CutDetailEntity cutDetailEntity = modelMapper.map(detalleCorteRequest, CutDetailEntity.class);
    cutDetailEntity.setElaboracion(productionEntity);
    productionEntity.getLote().setUnidades(productionEntity.getLote().getUnidades() + cutDetailEntity.getCantidad());

    productionEntity.setDetalleCorte(cutDetailEntity);
    return modelMapper.map(elaboracionRepository.save(productionEntity), ProductionResponse.class);
  }

  public ProductionResponse updateEmbolsado(LocalDate fechaEmbolsado, String idElaboracion) throws Exception {
    Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ProductionEntity productionEntity = elaboracionEntityOptional.get();

    if (fechaEmbolsado.isBefore(productionEntity.getFecha())) {
      throw new Exception("La fecha de embolsado debe ser posterior al inicio de la elaboración");
    } else if (productionEntity.getFechaSalidaMaduracion() == null || fechaEmbolsado.isBefore(productionEntity.getFechaSalidaMaduracion())) {
      throw new Exception("La fecha de embolsado debe ser posterior a la maduración");
    }

    if(productionEntity.getFormula().getTipoQueso().getItem().getNombre().equals(Cheese.Pategras.name())) {
      throw new Exception("El queso para el que está pensada la elaboración no permite tipos de cortes");
    }

    productionEntity.setFechaEmbolsado(fechaEmbolsado);

    return modelMapper.map(elaboracionRepository.save(productionEntity), ProductionResponse.class);
  }

  public ProductionResponse updateMaduracion(MadurationRequest maduracionRequest, String idElaboracion) throws Exception {
    Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ProductionEntity productionEntity = elaboracionEntityOptional.get();
    LocalDate        fechaEntrada     = maduracionRequest.getFechaEntrada();
    LocalDate        fechaSalida      = maduracionRequest.getFechaSalida();
    Integer          diasMaduracion   = productionEntity.getFormula().getTipoQueso().getDiasMaduracion();

    if (fechaEntrada.isBefore(productionEntity.getFecha())) {
      throw new Exception("La fecha de entrada debe ser posterior al inicio de la elaboración");
    }

    if (fechaSalida.isBefore(fechaEntrada.plusDays(diasMaduracion))) {
      throw new Exception("La fecha de salida debe ser al menos " + diasMaduracion.toString() + " días después de la fecha de entrada.");
    }

    productionEntity.setFechaEntradaMaduracion(fechaEntrada);
    productionEntity.setFechaSalidaMaduracion(fechaSalida);
    return modelMapper.map(elaboracionRepository.save(productionEntity), ProductionResponse.class);
  }

  public ProductionResponse updatePintado(LocalDate fechaPintado, String idElaboracion) throws Exception {
    Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ProductionEntity productionEntity = elaboracionEntityOptional.get();

    if (fechaPintado.isBefore(productionEntity.getFecha())) {
      throw new Exception("La fecha de pintado debe ser posterior al inicio de la elaboración");
    } else if (productionEntity.getFechaSalidaMaduracion() == null || fechaPintado.isBefore(productionEntity.getFechaSalidaMaduracion())) {
      throw new Exception("La fecha de pintado debe ser posterior a la maduración");
    }

    if(!productionEntity.getFormula().getTipoQueso().getItem().getNombre().equals(Cheese.Pategras.name())) {
      throw new Exception("El queso para el que está pensada la elaboración no permite tipos de cortes");
    }

    productionEntity.setFechaPintado(fechaPintado);

    return modelMapper.map(elaboracionRepository.save(productionEntity), ProductionResponse.class);
  }

  public ProductionResponse updateControl(QualityControlRequest controlCalidadRequest, String idElaboracion) throws Exception {
    Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findById(idElaboracion);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración");
    }

    ProductionEntity productionEntity = elaboracionEntityOptional.get();
    LocalDate        fechaControl     = controlCalidadRequest.getFecha();

    if (fechaControl.isBefore(productionEntity.getFecha())) {
      throw new Exception("La fecha de control de calidad debe ser posterior al inicio de la elaboración.");
    }

    LocalDate fechaEmbolsado = productionEntity.getFechaEmbolsado();
    LocalDate fechaPintado = productionEntity.getFechaPintado();

    if (fechaEmbolsado != null && fechaControl.isBefore(fechaEmbolsado)) {
      throw new Exception("La fecha de control de calidad debe ser posterior al embolsado.");
    }

    if (fechaPintado != null && fechaControl.isBefore(fechaPintado)) {
      throw new Exception("La fecha de control de calidad debe ser posterior al pintado.");
    }

    QualityControlEntity qualityControlEntity = modelMapper.map(controlCalidadRequest , QualityControlEntity.class);
    qualityControlEntity.setElaboracion(productionEntity);
    productionEntity.setControlCalidad(qualityControlEntity);
    productionEntity.getLote().setEstado(Status.Terminado.name());
    return modelMapper.map(elaboracionRepository.save(productionEntity), ProductionResponse.class);
  }

  public ProductionResponse deleteElaboracion(String id) {
    Optional<ProductionEntity> elaboracionEntityOptional = elaboracionRepository.findById(id);
    if (elaboracionEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se encontró la elaboración para eliminar");
    }

    ProductionEntity productionEntity = elaboracionEntityOptional.get();

    if (productionEntity.getLote().getUnidades() > 0) {
      throw new IllegalStateException("No se puede eliminar el item porque tiene lotes existentes.");
    }

    try{
      elaboracionRepository.delete(productionEntity);
      return modelMapper.map(productionEntity, ProductionResponse.class);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public ProductionReportResponse generateInforme(LocalDate fechaInicio, LocalDate fechaFin) {
    List<ProductionEntity> elaboracionEntities = elaboracionRepository.findByFechaBetween(fechaInicio, fechaFin);
    if (elaboracionEntities.isEmpty()) {
      elaboracionEntities = new ArrayList<>();
    }

    ProductionReportResponse informe = new ProductionReportResponse();
    informe.setCantidadElaboraciones(0);
    informe.setCantidadIncompletas(0);
    informe.setCantidadPategras(0);
    informe.setCantidadBarra(0);
    informe.setCantidadEnterosCremoso(0);
    informe.setCantidadMediosCremoso(0);
    informe.setCantidadCuartosCremoso(0);

    List<SuppliesReportDetailElaboracion> detallesInsumos = new ArrayList<>();

    List<ItemResponse> listaInsumos = insumoService.getItems();

    for (ItemResponse item : listaInsumos) {
      SuppliesReportDetailElaboracion detalleInsumo = new SuppliesReportDetailElaboracion();
      detalleInsumo.setInsumo(item);
      detalleInsumo.setTotal(0);

      detallesInsumos.add(detalleInsumo);
    }

    for(ProductionEntity elaboracion : elaboracionEntities) {
      for (FormulaDetailEntity detalleFormula : elaboracion.getFormula().getDetallesFormulas()) {
        for (SuppliesReportDetailElaboracion detalleInsumo : detallesInsumos) {
          if (detalleFormula.getInsumo().getNombre().equals(detalleInsumo.getInsumo().getNombreItem())) {
            detalleInsumo.setTotal(detalleInsumo.getTotal() + ((elaboracion.getCantidadLeche() / elaboracion.getFormula().getCantidadLeche()) * detalleFormula.getCantidad()));
          }
        }
      }

      informe.setCantidadElaboraciones(informe.getCantidadElaboraciones() + 1);

      if (elaboracion.getDetalleCorte() == null) {
        informe.setCantidadIncompletas(informe.getCantidadIncompletas() + 1);
      } else {
        if (elaboracion.getDetalleCorte().getCorte().equals(CutType.Whole.name())) {
          if (elaboracion.getFormula().getTipoQueso().getItem().getNombre().equals(Cheese.Pategras.name())) {
            informe.setCantidadPategras(informe.getCantidadPategras() + elaboracion.getDetalleCorte().getCantidad());
          } else if (elaboracion.getFormula().getTipoQueso().getItem().getNombre().equals(Cheese.Barra.name())) {
            informe.setCantidadBarra(informe.getCantidadBarra() + elaboracion.getDetalleCorte().getCantidad());
          } else {
            informe.setCantidadEnterosCremoso(informe.getCantidadEnterosCremoso() + elaboracion.getDetalleCorte().getCantidad());
          }
        } else if (elaboracion.getDetalleCorte().getCorte().equals(CutType.Medio.name())) {
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
