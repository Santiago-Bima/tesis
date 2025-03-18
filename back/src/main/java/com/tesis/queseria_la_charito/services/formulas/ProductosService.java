package com.tesis.queseria_la_charito.services.formulas;

import com.tesis.queseria_la_charito.dtos.response.formula.CheeseTypeResponse;
import com.tesis.queseria_la_charito.entities.formula.CheeseTypeEntity;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.formula.TipoQuesoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductosService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private TipoQuesoRepository tipoQuesoRepository;


    public List<CheeseTypeResponse> getAll() {
        List<CheeseTypeResponse> listaTiposQuesosResponse = new ArrayList<>();

        List<CheeseTypeEntity> tipoQuesoEntities = tipoQuesoRepository.findAll();
        if (tipoQuesoEntities.isEmpty()) {
            throw new EntityNotFoundException("No existen productos registrados");
        }

        for (CheeseTypeEntity cheeseTypeEntity : tipoQuesoEntities) {
            listaTiposQuesosResponse.add(modelMapper.map(cheeseTypeEntity, CheeseTypeResponse.class));
        }

        return listaTiposQuesosResponse;
    }

    public CheeseTypeResponse getById(Long id) {
        Optional<CheeseTypeEntity> tipoQuesoResponseOptional = tipoQuesoRepository.findById(id);
        if (tipoQuesoResponseOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el tipo de producto");
        }

        return modelMapper.map(tipoQuesoResponseOptional.get(), CheeseTypeResponse.class);
    }
}
