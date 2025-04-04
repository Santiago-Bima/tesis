package com.tesis.queseria_la_charito.services.purchase;

import com.tesis.queseria_la_charito.dtos.request.purchase.ProviderRequest;
import com.tesis.queseria_la_charito.dtos.response.purchase.ProviderResponse;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.purchase.ProviderEntity;
import com.tesis.queseria_la_charito.models.AccountType;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.purchase.ProviderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProviderService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private ProviderRepository repository;



  public List<ProviderResponse> getAll(Long idSupply) {
    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(idSupply);
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }

    List<ProviderEntity>   lstProviders         = repository.findBySupplyAndShow(itemEntityOptional.get(), true);
    List<ProviderResponse> lstProvidersResponse = new ArrayList<>();
    lstProviders.forEach(provider -> {
      ProviderResponse providerResponse = modelMapper.map(provider, ProviderResponse.class);
      lstProvidersResponse.add(providerResponse);
    });

    return lstProvidersResponse;
  }

  public ProviderResponse getById(Long id) {
    Optional<ProviderEntity> providerEntityOptional = repository.findByIdAndShow(id, true);
    if (providerEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    return modelMapper.map(providerEntityOptional.get(), ProviderResponse.class);
  }

  public ProviderResponse post(ProviderRequest provider) {
    ProviderEntity providerEntity = new ProviderEntity();
    providerEntity.setCuit(provider.getCuit());
    providerEntity.setEmail(provider.getEmail());
    providerEntity.setMeasurementUnit(provider.getMeasurementUnit());
    providerEntity.setMeasuredQuantity(provider.getMeasurementQuantity());
    providerEntity.setCost(provider.getCost());
    providerEntity.setBank(provider.getBank());
    providerEntity.setAlias(provider.getAlias());

    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(provider.getIdSupply());
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }
    providerEntity.setSupply(itemEntityOptional.get());

    providerEntity.setName(provider.getName());
    providerEntity.setPhone(provider.getPhone());
    providerEntity.setAccountType(provider.getAccountType().equals(AccountType.CheckingAccount.name()) ? "Cuenta Corriente" : "Caja de Ahorro");
    providerEntity.setShow(true);

    return modelMapper.map(repository.save(providerEntity), ProviderResponse.class);
  }

  public ProviderResponse put(ProviderRequest provider, Long id) {
    Optional<ProviderEntity> providerEntityOptional = repository.findByIdAndShow(id, true);
    if(providerEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el provider");
    }

    ProviderEntity providerEntity = providerEntityOptional.get();
    providerEntity.setCuit(provider.getCuit());
    providerEntity.setEmail(provider.getEmail());
    providerEntity.setCost(provider.getCost());
    providerEntity.setBank(provider.getBank());
    providerEntity.setAlias(provider.getAlias());
    providerEntity.setMeasurementUnit(provider.getMeasurementUnit());
    providerEntity.setMeasuredQuantity(provider.getMeasurementQuantity());

    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(provider.getIdSupply());
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }
    providerEntity.setSupply(itemEntityOptional.get());

    providerEntity.setName(provider.getName());
    providerEntity.setPhone(provider.getPhone());
    providerEntity.setAccountType(provider.getAccountType());

    return modelMapper.map(repository.save(providerEntity), ProviderResponse.class);
  }

  public ProviderResponse delete(Long id) {
    Optional<ProviderEntity> providerEntityOptional = repository.findByIdAndShow(id, true);
    if (providerEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    ProviderEntity providerEntity = providerEntityOptional.get();
    providerEntity.setShow(false);

    return modelMapper.map(repository.save(providerEntity), ProviderResponse.class);
  }
}
