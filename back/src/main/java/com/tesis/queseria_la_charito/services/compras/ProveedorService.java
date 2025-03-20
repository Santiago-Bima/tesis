package com.tesis.queseria_la_charito.services.compras;

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
public class ProveedorService {
  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private ItemRepository itemRepository;

  @Autowired
  private ProviderRepository repository;



  public List<ProviderResponse> getAll(Long idInsumo) {
    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(idInsumo);
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }

    List<ProviderEntity>   lstProveedores         = repository.findByInsumoAndMostrar(itemEntityOptional.get(), true);
    List<ProviderResponse> lstProveedoresResponse = new ArrayList<>();
    lstProveedores.forEach(proveedor -> {
      ProviderResponse proveedorResponse = modelMapper.map(proveedor, ProviderResponse.class);
      lstProveedoresResponse.add(proveedorResponse);
    });

    return lstProveedoresResponse;
  }

  public ProviderResponse getById(Long id) {
    Optional<ProviderEntity> proveedorEntityOptional = repository.findByIdAndMostrar(id, true);
    if (proveedorEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    return modelMapper.map(proveedorEntityOptional.get(), ProviderResponse.class);
  }

  public ProviderResponse post(ProviderRequest proveedor) {
    ProviderEntity providerEntity = new ProviderEntity();
    providerEntity.setCuit(proveedor.getCuit());
    providerEntity.setEmail(proveedor.getEmail());
    providerEntity.setUnidadMedida(proveedor.getUnidadMedida());
    providerEntity.setCantidadMedida(proveedor.getCantidadMedida());
    providerEntity.setCosto(proveedor.getCosto());
    providerEntity.setBanco(proveedor.getBanco());
    providerEntity.setAlias(proveedor.getAlias());

    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(proveedor.getIdInsumo());
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }
    providerEntity.setInsumo(itemEntityOptional.get());

    providerEntity.setNombre(proveedor.getNombre());
    providerEntity.setTelefono(proveedor.getTelefono());
    providerEntity.setTipoCuenta(proveedor.getTipoCuenta().equals(AccountType.Corriente.name()) ? "Cuenta Corriente" : "Caja de Ahorro");
    providerEntity.setMostrar(true);

    return modelMapper.map(repository.save(providerEntity), ProviderResponse.class);
  }

  public ProviderResponse put(ProviderRequest proveedor, Long id) {
    Optional<ProviderEntity> proveedorEntityOptional = repository.findByIdAndMostrar(id, true);
    if(proveedorEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    ProviderEntity providerEntity = proveedorEntityOptional.get();
    providerEntity.setCuit(proveedor.getCuit());
    providerEntity.setEmail(proveedor.getEmail());
    providerEntity.setCosto(proveedor.getCosto());
    providerEntity.setBanco(proveedor.getBanco());
    providerEntity.setAlias(proveedor.getAlias());
    providerEntity.setUnidadMedida(proveedor.getUnidadMedida());
    providerEntity.setCantidadMedida(proveedor.getCantidadMedida());

    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(proveedor.getIdInsumo());
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }
    providerEntity.setInsumo(itemEntityOptional.get());

    providerEntity.setNombre(proveedor.getNombre());
    providerEntity.setTelefono(proveedor.getTelefono());
    providerEntity.setTipoCuenta(proveedor.getTipoCuenta());

    return modelMapper.map(repository.save(providerEntity), ProviderResponse.class);
  }

  public ProviderResponse delete(Long id) {
    Optional<ProviderEntity> proveedorEntityOptional = repository.findByIdAndMostrar(id, true);
    if (proveedorEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    ProviderEntity providerEntity = proveedorEntityOptional.get();
    providerEntity.setMostrar(false);

    return modelMapper.map(repository.save(providerEntity), ProviderResponse.class);
  }
}
