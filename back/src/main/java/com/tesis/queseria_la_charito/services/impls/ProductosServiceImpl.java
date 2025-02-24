package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.response.formula.TipoQuesoResponse;
import com.tesis.queseria_la_charito.entities.formula.TipoQuesoEntity;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.formula.TipoQuesoRepository;
import com.tesis.queseria_la_charito.services.ProductoService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductosServiceImpl implements ProductoService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TipoQuesoRepository tipoQuesoRepository;

    @Override
    public List<TipoQuesoResponse> getAll() {
        List<TipoQuesoResponse> listaTiposQuesosResponse = new ArrayList<>();

        List<TipoQuesoEntity> tipoQuesoEntities = tipoQuesoRepository.findAll();
        if (tipoQuesoEntities.isEmpty()) {
            throw new EntityNotFoundException("No existen productos registrados");
        }

        for (TipoQuesoEntity tipoQuesoEntity : tipoQuesoEntities) {
            listaTiposQuesosResponse.add(modelMapper.map(tipoQuesoEntity, TipoQuesoResponse.class));
        }

        return listaTiposQuesosResponse;
    }

    @Override
    public TipoQuesoResponse getById(Long id) {
        Optional<TipoQuesoEntity> tipoQuesoResponseOptional = tipoQuesoRepository.findById(id);
        if (tipoQuesoResponseOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el tipo de producto");
        }

        return modelMapper.map(tipoQuesoResponseOptional.get(), TipoQuesoResponse.class);
    }
}
