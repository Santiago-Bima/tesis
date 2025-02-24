package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.request.compra.ComprobanteCompraRequest;
import com.tesis.queseria_la_charito.dtos.request.compra.DetalleCompraRequest;
import com.tesis.queseria_la_charito.dtos.response.compra.ComprobanteCompraResponse;
import com.tesis.queseria_la_charito.dtos.response.compra.DetalleComprobanteResponse;
import com.tesis.queseria_la_charito.dtos.response.compra.InformeCompraResponse;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.LoteEntity;
import com.tesis.queseria_la_charito.entities.compra.ComprobanteCompraEntity;
import com.tesis.queseria_la_charito.entities.compra.DetalleComprobanteEntity;
import com.tesis.queseria_la_charito.entities.compra.ProveedorEntity;
import com.tesis.queseria_la_charito.models.TipoItem;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.compra.CompraRepository;
import com.tesis.queseria_la_charito.repositories.compra.DetalleCompraRepository;
import com.tesis.queseria_la_charito.repositories.compra.ProveedorRepository;
import com.tesis.queseria_la_charito.services.CompraService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompraServiceImpl implements CompraService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private LoteServiceImpl loteService;

  @Autowired
  private CompraRepository compraRepository;

  @Autowired
  private DetalleCompraRepository detalleCompraRepository;

  @Autowired
  private ProveedorRepository proveedorRepository;

  @Autowired
  private ItemRepository itemRepository;

  @Override
  public List<ComprobanteCompraResponse> getAll(LocalDate fecha) {
    List<ComprobanteCompraEntity> comprobanteCompraEntityList = compraRepository.findAllByFecha(fecha.plusDays(1));
    if (comprobanteCompraEntityList.isEmpty()) {
      return new ArrayList<>();
    }
    List<ComprobanteCompraResponse> comprobanteCompraResponses = new ArrayList<>();

    for(ComprobanteCompraEntity comprobanteCompraEntity : comprobanteCompraEntityList) {
      List<DetalleComprobanteEntity> detalleComprobanteEntities = detalleCompraRepository.findAllByComprobante(comprobanteCompraEntity);
      List<DetalleComprobanteResponse> detalleComprobanteResponses = new ArrayList<>();
      for(DetalleComprobanteEntity detalle : detalleComprobanteEntities) {
        detalleComprobanteResponses.add(modelMapper.map(detalle, DetalleComprobanteResponse.class));
      }

      ComprobanteCompraResponse comprobanteCompraResponse = modelMapper.map(comprobanteCompraEntity, ComprobanteCompraResponse.class);
      comprobanteCompraResponse.setDetalles(detalleComprobanteResponses);
      comprobanteCompraResponses.add(comprobanteCompraResponse);
    }

    return comprobanteCompraResponses;
  }

  @Override
  public ComprobanteCompraResponse post(ComprobanteCompraRequest comprobante) {
    ComprobanteCompraEntity comprobanteCompraEntity = new ComprobanteCompraEntity();


    List<DetalleComprobanteEntity> detalleComprobanteEntities = new ArrayList<>();
    for(DetalleCompraRequest detalle : comprobante.getListDetalles()) {
      DetalleComprobanteEntity detalleComprobante = new DetalleComprobanteEntity();
      detalleComprobante.setCantidad(detalle.getCantidad());
      detalleComprobante.setSubtotal(detalle.getSubtotal());

      Optional<ProveedorEntity> proveedorEntityOptional = proveedorRepository.findByIdAndMostrar(detalle.getIdProveedor(), true);
      if (proveedorEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se ha encontrado el proveedor");
      }
      ProveedorEntity proveedor = proveedorEntityOptional.get();
      detalleComprobante.setProveedor(proveedor);

      ItemEntity itemEntity = proveedor.getInsumo();
      Integer    cantidad   = getCantidad(detalle, itemEntity, proveedor);

      LoteEntity loteEntity = modelMapper.map(loteService.postLote(itemEntity.getId(), cantidad), LoteEntity.class);
      detalleComprobante.setLote(loteEntity);
      detalleComprobante.setComprobante(comprobanteCompraEntity);

      detalleComprobanteEntities.add(detalleComprobante);
    }

    comprobanteCompraEntity.setTotal(comprobante.getTotal());
    comprobanteCompraEntity.setFecha(comprobante.getFecha());
    comprobanteCompraEntity.setListaDetalles(detalleComprobanteEntities);

    return modelMapper.map(compraRepository.save(comprobanteCompraEntity), ComprobanteCompraResponse.class);
  }

  @Override
  public List<InformeCompraResponse> generateInforme(LocalDate fechaInicio, LocalDate fechaFin) {
    List<InformeCompraResponse> informes = new ArrayList<>();

    List<ItemEntity> itemResponseList = itemRepository.findByTipo(TipoItem.Insumo.name());

    for (ItemEntity item : itemResponseList) {
      InformeCompraResponse informe = new InformeCompraResponse();

      List<DetalleComprobanteEntity> detalleComprobanteEntities = detalleCompraRepository.findAllByProveedorInsumoAndComprobanteFechaBetween(item, fechaInicio, fechaFin);
      List<DetalleComprobanteResponse> detalleComprobanteResponses = new ArrayList<>();

      int total = 0;

      for (DetalleComprobanteEntity detalle : detalleComprobanteEntities) {
        total += detalle.getSubtotal();
        detalleComprobanteResponses.add(modelMapper.map(detalle, DetalleComprobanteResponse.class));
      }


      informe.setInsumo(item.getNombre());
      informe.setTotal(total);
      informe.setDetalles(detalleComprobanteResponses);
      informes.add(informe);
    }

    return informes;
  }

  private static Integer getCantidad(DetalleCompraRequest detalle, ItemEntity itemEntity, ProveedorEntity proveedor) {
    String unidadMedida = itemEntity.getUnidadMedida();
    String unidadMedidaCompra = proveedor.getUnidadMedida();
    int    cantidad           = 0;

    if ((unidadMedida.equals("g") && unidadMedidaCompra.equals("kg")) || (unidadMedida.equals("ml") && unidadMedidaCompra.equals("l"))) {
      cantidad = detalle.getCantidad() * 1000;
    } else if ((unidadMedida.equals("kg") && unidadMedidaCompra.equals("g")) || (unidadMedida.equals("l") && unidadMedidaCompra.equals("ml"))) {
      cantidad = detalle.getCantidad() / 1000;
    } else {
      cantidad = detalle.getCantidad();
    }
    return cantidad;
  }
}
