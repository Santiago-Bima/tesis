package com.tesis.queseria_la_charito.services.compras;

import com.tesis.queseria_la_charito.dtos.request.purchase.ReceipRequest;
import com.tesis.queseria_la_charito.dtos.request.purchase.ReceipDetailRequest;
import com.tesis.queseria_la_charito.dtos.response.purchase.ReceipResponse;
import com.tesis.queseria_la_charito.dtos.response.purchase.ReceipDetailResponse;
import com.tesis.queseria_la_charito.dtos.response.purchase.ReceipReportResponse;
import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.purchase.ReceiptEntity;
import com.tesis.queseria_la_charito.entities.purchase.ReceiptDetailEntity;
import com.tesis.queseria_la_charito.entities.purchase.ProviderEntity;
import com.tesis.queseria_la_charito.models.ItemType;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.purchase.PurchaseRepository;
import com.tesis.queseria_la_charito.repositories.purchase.PurchaseDetailRepository;
import com.tesis.queseria_la_charito.repositories.purchase.ProviderRepository;
import com.tesis.queseria_la_charito.services.lotes.LoteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CompraService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private LoteService loteService;

  @Autowired
  private PurchaseRepository purchaseRepository;

  @Autowired
  private PurchaseDetailRepository purchaseDetailRepository;

  @Autowired
  private ProviderRepository providerRepository;

  @Autowired
  private ItemRepository itemRepository;



  public List<ReceipResponse> getAll(LocalDate fecha) {
    List<ReceiptEntity> receiptEntityList = purchaseRepository.findAllByFecha(fecha.plusDays(1));
    if (receiptEntityList.isEmpty()) {
      return new ArrayList<>();
    }
    List<ReceipResponse> comprobanteCompraResponses = new ArrayList<>();

    for(ReceiptEntity receiptEntity : receiptEntityList) {
      List<ReceiptDetailEntity>  detalleComprobanteEntities  = purchaseDetailRepository.findAllByComprobante(receiptEntity);
      List<ReceipDetailResponse> detalleComprobanteResponses = new ArrayList<>();
      for(ReceiptDetailEntity detalle : detalleComprobanteEntities) {
        detalleComprobanteResponses.add(modelMapper.map(detalle, ReceipDetailResponse.class));
      }

      ReceipResponse comprobanteCompraResponse = modelMapper.map(receiptEntity, ReceipResponse.class);
      comprobanteCompraResponse.setDetalles(detalleComprobanteResponses);
      comprobanteCompraResponses.add(comprobanteCompraResponse);
    }

    return comprobanteCompraResponses;
  }

  public ReceipResponse post(ReceipRequest comprobante) {
    ReceiptEntity receiptEntity = new ReceiptEntity();


    List<ReceiptDetailEntity> detalleComprobanteEntities = new ArrayList<>();
    for(ReceipDetailRequest detalle : comprobante.getLstDetails()) {
      ReceiptDetailEntity detalleComprobante = new ReceiptDetailEntity();
      detalleComprobante.setCantidad(detalle.getCantidad());
      detalleComprobante.setSubtotal(detalle.getSubtotal());

      Optional<ProviderEntity> proveedorEntityOptional = providerRepository.findByIdAndMostrar(detalle.getIdProveedor(), true);
      if (proveedorEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se ha encontrado el proveedor");
      }
      ProviderEntity proveedor = proveedorEntityOptional.get();
      detalleComprobante.setProveedor(proveedor);

      ItemEntity itemEntity = proveedor.getInsumo();
      Integer    cantidad   = getCantidad(detalle, itemEntity, proveedor);

      BatchEntity batchEntity = modelMapper.map(loteService.postLote(itemEntity.getId(), cantidad), BatchEntity.class);
      detalleComprobante.setLote(batchEntity);
      detalleComprobante.setComprobante(receiptEntity);

      detalleComprobanteEntities.add(detalleComprobante);
    }

    receiptEntity.setTotal(comprobante.getTotal());
    receiptEntity.setFecha(comprobante.getDate());
    receiptEntity.setListaDetalles(detalleComprobanteEntities);

    return modelMapper.map(purchaseRepository.save(receiptEntity), ReceipResponse.class);
  }

  public List<ReceipReportResponse> generateInforme(LocalDate fechaInicio, LocalDate fechaFin) {
    List<ReceipReportResponse> informes = new ArrayList<>();

    List<ItemEntity> itemResponseList = itemRepository.findByTipo(ItemType.Insumo.name());

    for (ItemEntity item : itemResponseList) {
      ReceipReportResponse informe = new ReceipReportResponse();

      List<ReceiptDetailEntity>  detalleComprobanteEntities  = purchaseDetailRepository.findAllByProveedorInsumoAndComprobanteFechaBetween(item, fechaInicio, fechaFin);
      List<ReceipDetailResponse> detalleComprobanteResponses = new ArrayList<>();

      int total = 0;

      for (ReceiptDetailEntity detalle : detalleComprobanteEntities) {
        total += detalle.getSubtotal();
        detalleComprobanteResponses.add(modelMapper.map(detalle, ReceipDetailResponse.class));
      }


      informe.setInsumo(item.getNombre());
      informe.setTotal(total);
      informe.setDetalles(detalleComprobanteResponses);
      informes.add(informe);
    }

    return informes;
  }

  private static Integer getCantidad(ReceipDetailRequest detalle, ItemEntity itemEntity, ProviderEntity proveedor) {
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
