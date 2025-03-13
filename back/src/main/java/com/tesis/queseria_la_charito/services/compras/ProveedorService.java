package com.tesis.queseria_la_charito.services.compras;

import com.tesis.queseria_la_charito.dtos.request.purchase.ProviderRequest;
import com.tesis.queseria_la_charito.dtos.response.purchase.ProviderResponse;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.compra.ProveedorEntity;
import com.tesis.queseria_la_charito.models.TipoCuenta;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.compra.ProveedorRepository;
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
  private ProveedorRepository repository;



  public List<ProviderResponse> getAll(Long idInsumo) {
    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(idInsumo);
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }

    List<ProveedorEntity>  lstProveedores         = repository.findByInsumoAndMostrar(itemEntityOptional.get(), true);
    List<ProviderResponse> lstProveedoresResponse = new ArrayList<>();
    lstProveedores.forEach(proveedor -> {
      ProviderResponse proveedorResponse = modelMapper.map(proveedor, ProviderResponse.class);
      lstProveedoresResponse.add(proveedorResponse);
    });

    return lstProveedoresResponse;
  }

  public ProviderResponse getById(Long id) {
    Optional<ProveedorEntity> proveedorEntityOptional = repository.findByIdAndMostrar(id, true);
    if (proveedorEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    return modelMapper.map(proveedorEntityOptional.get(), ProviderResponse.class);
  }

  public ProviderResponse post(ProviderRequest proveedor) {
    ProveedorEntity proveedorEntity = new ProveedorEntity();
    proveedorEntity.setCuit(proveedor.getCuit());
    proveedorEntity.setEmail(proveedor.getEmail());
    proveedorEntity.setUnidadMedida(proveedor.getUnidadMedida());
    proveedorEntity.setCantidadMedida(proveedor.getCantidadMedida());
    proveedorEntity.setCosto(proveedor.getCosto());
    proveedorEntity.setBanco(proveedor.getBanco());
    proveedorEntity.setAlias(proveedor.getAlias());

    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(proveedor.getIdInsumo());
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }
    proveedorEntity.setInsumo(itemEntityOptional.get());

    proveedorEntity.setNombre(proveedor.getNombre());
    proveedorEntity.setTelefono(proveedor.getTelefono());
    proveedorEntity.setTipoCuenta(proveedor.getTipoCuenta().equals(TipoCuenta.Corriente.name()) ? "Cuenta Corriente" : "Caja de Ahorro");
    proveedorEntity.setMostrar(true);

    return modelMapper.map(repository.save(proveedorEntity), ProviderResponse.class);
  }

  public ProviderResponse put(ProviderRequest proveedor, Long id) {
    Optional<ProveedorEntity> proveedorEntityOptional = repository.findByIdAndMostrar(id, true);
    if(proveedorEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    ProveedorEntity proveedorEntity = proveedorEntityOptional.get();
    proveedorEntity.setCuit(proveedor.getCuit());
    proveedorEntity.setEmail(proveedor.getEmail());
    proveedorEntity.setCosto(proveedor.getCosto());
    proveedorEntity.setBanco(proveedor.getBanco());
    proveedorEntity.setAlias(proveedor.getAlias());
    proveedorEntity.setUnidadMedida(proveedor.getUnidadMedida());
    proveedorEntity.setCantidadMedida(proveedor.getCantidadMedida());

    Optional<ItemEntity> itemEntityOptional = itemRepository.findById(proveedor.getIdInsumo());
    if (itemEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el insumo");
    }
    proveedorEntity.setInsumo(itemEntityOptional.get());

    proveedorEntity.setNombre(proveedor.getNombre());
    proveedorEntity.setTelefono(proveedor.getTelefono());
    proveedorEntity.setTipoCuenta(proveedor.getTipoCuenta());

    return modelMapper.map(repository.save(proveedorEntity), ProviderResponse.class);
  }

  public ProviderResponse delete(Long id) {
    Optional<ProveedorEntity> proveedorEntityOptional = repository.findByIdAndMostrar(id, true);
    if (proveedorEntityOptional.isEmpty()) {
      throw new EntityNotFoundException("No se ha encontrado el proveedor");
    }

    ProveedorEntity proveedorEntity = proveedorEntityOptional.get();
    proveedorEntity.setMostrar(false);

    return modelMapper.map(repository.save(proveedorEntity), ProviderResponse.class);
  }
}
