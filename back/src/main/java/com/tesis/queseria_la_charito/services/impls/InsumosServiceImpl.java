package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.request.ItemRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.models.TipoItem;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.LoteRepository;
import com.tesis.queseria_la_charito.services.InsumoService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class InsumosServiceImpl implements InsumoService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<ItemResponse> getItems() {
        List<ItemResponse> listaInsumosResponse = new ArrayList<>();
        List<ItemEntity> listaInsumosEntity = itemRepository.findByTipo(TipoItem.Insumo.name());
        if (listaInsumosEntity.isEmpty()) {
            return new ArrayList<>();
        }

        for (ItemEntity itemEntity : listaInsumosEntity) {
            listaInsumosResponse.add(modelMapper.map(itemEntity, ItemResponse.class));
        }

        return listaInsumosResponse;
    }

    @Override
    public ItemResponse getItemById(Long id) {
        Optional<ItemEntity> itemEntityOptional = itemRepository.findById(id);
        if (itemEntityOptional.isEmpty()) {
            throw new EntityNotFoundException();
        }

        return modelMapper.map(itemEntityOptional.get(), ItemResponse.class);
    }

    @Override
    public ItemResponse postItem(ItemRequest item) throws Exception {
        ItemEntity itemEntity = modelMapper.map(item, ItemEntity.class);
        itemEntity.setTipo(TipoItem.Insumo.name());

        Optional<ItemEntity> itemEntityOptional = itemRepository.findByNombre(itemEntity.getNombre());
        if (itemEntityOptional.isPresent()) {
            throw new EntityExistsException("Ya existe un insumo/producto con el mismo nombre");
        }

        return modelMapper.map(itemRepository.save(itemEntity), ItemResponse.class);
    }

    @Override
    public ItemResponse deleteItem(Long id) {
        Optional<ItemEntity> itemEntityOptional = itemRepository.findById(id);
        if (itemEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el Insumo");
        }

        ItemEntity itemEntity = itemEntityOptional.get();

        if (!itemEntity.getListaDetalles().isEmpty() || !itemEntity.getListaLotes().isEmpty() || !itemEntity.getListaProveedores().isEmpty()) {
            throw new IllegalStateException("No se puede eliminar el item porque tiene dependencias activas.");
        }

        try{
            itemRepository.delete(itemEntity);
            return modelMapper.map(itemEntity, ItemResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ItemResponse putItem(ItemRequest item, Long id) {
        ItemEntity itemEntity = modelMapper.map(item, ItemEntity.class);
        itemEntity.setId(id);
        itemEntity.setTipo(TipoItem.Insumo.name());

        Optional<ItemEntity> itemEntityOptional = itemRepository.findByNombre(itemEntity.getNombre());
        if (itemEntityOptional.isPresent() && !Objects.equals(itemEntityOptional.get().getId(), id)) {
            throw new EntityExistsException("Ya existe un insumo/producto con el mismo nombre");
        }

        try {
            itemRepository.save(itemEntity);
            return modelMapper.map(itemEntity, ItemResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
