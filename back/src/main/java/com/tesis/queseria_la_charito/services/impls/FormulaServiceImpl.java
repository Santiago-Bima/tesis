package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.request.formula.DetalleFormulaRequest;
import com.tesis.queseria_la_charito.dtos.request.formula.FormulaRequest;
import com.tesis.queseria_la_charito.dtos.response.formula.DetalleFormulaResponse;
import com.tesis.queseria_la_charito.dtos.response.formula.FormulaResponse;
import com.tesis.queseria_la_charito.entities.formula.DetalleFormulaEntity;
import com.tesis.queseria_la_charito.entities.formula.FormulaEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.formula.TipoQuesoEntity;
import com.tesis.queseria_la_charito.repositories.formula.DetalleFormulaRepository;
import com.tesis.queseria_la_charito.repositories.formula.FormulaRepository;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.formula.TipoQuesoRepository;
import com.tesis.queseria_la_charito.services.FormulaService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class FormulaServiceImpl implements FormulaService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FormulaRepository formulaRepository;

    @Autowired
    private DetalleFormulaRepository detalleFormulaRepository;

    @Autowired
    private TipoQuesoRepository tipoQuesoRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Override
    public List<FormulaResponse> getFormulasByProducto(Long tipoProductoId) {
        Optional<TipoQuesoEntity> productoEntityOptional = tipoQuesoRepository.findById(tipoProductoId);
        if (productoEntityOptional.isEmpty()){
            throw new EntityNotFoundException();
        }
        
        TipoQuesoEntity tipoQuesoEntity = productoEntityOptional.get();
        List<FormulaResponse> listaFormulasResponse = new ArrayList<>();
        List<FormulaEntity> listaFormulasEntity = formulaRepository.findAllByTipoQueso(tipoQuesoEntity);

        for (FormulaEntity formulaEntity : listaFormulasEntity){
            List<DetalleFormulaEntity> listaDetallesEntity = detalleFormulaRepository.findAllByFormula(formulaEntity);
            List<DetalleFormulaResponse> listaDetallesResponse = new ArrayList<>();
            FormulaResponse formulaResponse = modelMapper.map(formulaEntity, FormulaResponse.class);
            for (DetalleFormulaEntity detalleEntity : listaDetallesEntity){
                listaDetallesResponse.add(modelMapper.map(detalleEntity, DetalleFormulaResponse.class));
            }
            formulaResponse.setDetallesFormulas(listaDetallesResponse);
            listaFormulasResponse.add(formulaResponse);
        }
        return listaFormulasResponse;
    }

    @Override
    public FormulaResponse getFormulaById(String id) {
        Optional<FormulaEntity> formulaEntity = formulaRepository.findById(id);
        if(formulaEntity.isEmpty()){
            throw new EntityNotFoundException();
        }
        FormulaResponse formulaResponse = modelMapper.map(formulaEntity.get(), FormulaResponse.class);

        List<DetalleFormulaEntity> listaDetallesEntity = detalleFormulaRepository.findAllByFormula(formulaEntity.get());
        List<DetalleFormulaResponse> listaDetallesResponse = new ArrayList<>();

        for (DetalleFormulaEntity detalleEntity : listaDetallesEntity){
            listaDetallesResponse.add(modelMapper.map(detalleEntity, DetalleFormulaResponse.class));
        }

        formulaResponse.setDetallesFormulas(listaDetallesResponse);

        return formulaResponse;
    }

    @Override
    public FormulaResponse postFormula(FormulaRequest formulaRequest) {
        if (formulaRepository.findById(formulaRequest.getCodigo()).isPresent()) {
            throw new EntityExistsException("Ya existe una fórmula con el mismo código");
        }

        Optional<TipoQuesoEntity> tipoQuesoEntityOptional = tipoQuesoRepository.findById(formulaRequest.getTipoQueso());
        if (tipoQuesoEntityOptional.isEmpty()){
            throw new EntityNotFoundException("No se encontró el tipo de producto");
        }

        FormulaEntity formulaEntity = modelMapper.map(formulaRequest, FormulaEntity.class);
        formulaEntity.setTipoQueso(tipoQuesoEntityOptional.get());

        for (int i = 0; i < formulaEntity.getDetallesFormulas().size(); i++) {
            formulaEntity.getDetallesFormulas().get(i).setFormula(formulaEntity);
            Optional<ItemEntity> insumoEntity = itemRepository.findById(formulaRequest.getDetallesFormulas().get(i).getInsumo());
            if (insumoEntity.isEmpty()) {
                throw new EntityNotFoundException("No se encontró el insumo");
            }

            formulaEntity.getDetallesFormulas().get(i).setInsumo(insumoEntity.get());
        }

        FormulaEntity formulaEntitySaved = formulaRepository.save(formulaEntity);
        formulaEntitySaved.setDetallesFormulas(formulaEntity.getDetallesFormulas());

        return modelMapper.map(formulaEntitySaved, FormulaResponse.class);
    }

    @Override
    public FormulaResponse putFormula(FormulaRequest formulaRequest, String id) {
        Optional<FormulaEntity> formulaEntityAntigua = formulaRepository.findById(id);
        if (formulaEntityAntigua.isEmpty()) {
            throw new EntityNotFoundException("No se encontró la fórmula para editar");
        }

        FormulaEntity formulaEntity = formulaEntityAntigua.get();
        formulaEntity.setCantidadLeche(formulaRequest.getCantidadLeche());

        Optional<TipoQuesoEntity> tipoQuesoEntityOptional = tipoQuesoRepository.findById(formulaRequest.getTipoQueso());
        if (tipoQuesoEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el tipo de producto");
        }
        formulaEntity.setTipoQueso(tipoQuesoEntityOptional.get());

        formulaEntity.getDetallesFormulas().clear();

        for (DetalleFormulaRequest detalleFormulaRequest : formulaRequest.getDetallesFormulas()) {
            Optional<ItemEntity> insumoEntity = itemRepository.findById(detalleFormulaRequest.getInsumo());
            if (insumoEntity.isEmpty()) {
                throw new EntityNotFoundException("No se encontró el insumo");
            }

            DetalleFormulaEntity detalleFormulaEntity = modelMapper.map(detalleFormulaRequest, DetalleFormulaEntity.class);
            detalleFormulaEntity.setInsumo(insumoEntity.get());
            detalleFormulaEntity.setFormula(formulaEntity);
            formulaEntity.getDetallesFormulas().add(detalleFormulaEntity);
        }

        FormulaEntity formulaEntityFinal = formulaRepository.save(formulaEntity);

        return modelMapper.map(formulaEntityFinal, FormulaResponse.class);
    }


    @Override
    public FormulaResponse deleteFormula(String id) {
        Optional<FormulaEntity> formulaEntityOptional = formulaRepository.findById(id);
        if (formulaEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró la fórmula para eliminar");
        }

        try {
            formulaRepository.delete(formulaEntityOptional.get());
        } catch (Exception e) {
            throw new RuntimeException("Hubo un error al intentar eliminar la fórmula", e);
        }

        return modelMapper.map(formulaEntityOptional.get(), FormulaResponse.class);
    }
}
