package com.tesis.queseria_la_charito.services.purchase;

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
public class PurchaseService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private LoteService batchService;

  @Autowired
  private PurchaseRepository purchaseRepository;

  @Autowired
  private PurchaseDetailRepository purchaseDetailRepository;

  @Autowired
  private ProviderRepository providerRepository;

  @Autowired
  private ItemRepository itemRepository;



  public List<ReceipResponse> getAll(LocalDate date) {
    List<ReceiptEntity> receiptEntityList = purchaseRepository.findAllByDate(date.plusDays(1));
    if (receiptEntityList.isEmpty()) {
      return new ArrayList<>();
    }
    List<ReceipResponse> receiptsResponses = new ArrayList<>();

    for(ReceiptEntity receiptEntity : receiptEntityList) {
      List<ReceiptDetailEntity>  receiptDetailsEntities  = purchaseDetailRepository.findAllByReceipt(receiptEntity);
      List<ReceipDetailResponse> receiptDetailsResponses = new ArrayList<>();
      for(ReceiptDetailEntity detail : receiptDetailsEntities) {
        receiptDetailsResponses.add(modelMapper.map(detail, ReceipDetailResponse.class));
      }

      ReceipResponse receiptResponse = modelMapper.map(receiptEntity, ReceipResponse.class);
      receiptResponse.setLstDetails(receiptDetailsResponses);
      receiptsResponses.add(receiptResponse);
    }

    return receiptsResponses;
  }

  public ReceipResponse post(ReceipRequest receipt) {
    ReceiptEntity receiptEntity = new ReceiptEntity();


    List<ReceiptDetailEntity> receiptDetailsEntities = new ArrayList<>();
    for(ReceipDetailRequest detail : receipt.getLstDetails()) {
      ReceiptDetailEntity receiptDetail = new ReceiptDetailEntity();
      receiptDetail.setQuantity(detail.getQuantity());
      receiptDetail.setSubtotal(detail.getSubtotal());

      Optional<ProviderEntity> providerEntityOptional = providerRepository.findByIdAndShow(detail.getIdProvider(), true);
      if (providerEntityOptional.isEmpty()) {
        throw new EntityNotFoundException("No se ha encontrado el provider");
      }
      ProviderEntity provider = providerEntityOptional.get();
      receiptDetail.setProvider(provider);

      ItemEntity itemEntity = provider.getSupply();
      Integer    quantity   = getQuantity(detail, itemEntity, provider);

      BatchEntity batchEntity = modelMapper.map(batchService.postLote(itemEntity.getId(), quantity), BatchEntity.class);
      receiptDetail.setBatch(batchEntity);
      receiptDetail.setReceipt(receiptEntity);

      receiptDetailsEntities.add(receiptDetail);
    }

    receiptEntity.setTotal(receipt.getTotal());
    receiptEntity.setDate(receipt.getDate());
    receiptEntity.setLstDetails(receiptDetailsEntities);

    return modelMapper.map(purchaseRepository.save(receiptEntity), ReceipResponse.class);
  }

  public List<ReceipReportResponse> generateReport(LocalDate begginingDate, LocalDate endDate) {
    List<ReceipReportResponse> reports = new ArrayList<>();

    List<ItemEntity> itemResponseList = itemRepository.findByType(ItemType.Supply.name());

    for (ItemEntity item : itemResponseList) {
      ReceipReportResponse report = new ReceipReportResponse();

      List<ReceiptDetailEntity>  receiptDetailsEntities  = purchaseDetailRepository.findAllByProviderSupplyAndReceiptDateBetween(item, begginingDate, endDate);
      List<ReceipDetailResponse> receiptDetailsResponses = new ArrayList<>();

      int total = 0;

      for (ReceiptDetailEntity detail : receiptDetailsEntities) {
        total += detail.getSubtotal();
        receiptDetailsResponses.add(modelMapper.map(detail, ReceipDetailResponse.class));
      }


      report.setSupply(item.getName());
      report.setTotal(total);
      report.setLstDetails(receiptDetailsResponses);
      reports.add(report);
    }

    return reports;
  }

  private static Integer getQuantity(ReceipDetailRequest detail, ItemEntity itemEntity, ProviderEntity provider) {
    String measurementUnit = itemEntity.getMeasurementUnit();
    String receiptMeasurementUnit = provider.getMeasurementUnit();
    int    quantity           = 0;

    if ((measurementUnit.equals("g") && receiptMeasurementUnit.equals("kg")) || (measurementUnit.equals("ml") && receiptMeasurementUnit.equals("l"))) {
      quantity = detail.getQuantity() * 1000;
    } else if ((measurementUnit.equals("kg") && receiptMeasurementUnit.equals("g")) || (measurementUnit.equals("l") && receiptMeasurementUnit.equals("ml"))) {
      quantity = detail.getQuantity() / 1000;
    } else {
      quantity = detail.getQuantity();
    }
    return quantity;
  }
}
