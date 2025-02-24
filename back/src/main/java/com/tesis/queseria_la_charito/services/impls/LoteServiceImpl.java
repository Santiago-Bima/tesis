package com.tesis.queseria_la_charito.services.impls;

import com.tesis.queseria_la_charito.dtos.request.LoteRequest;
import com.tesis.queseria_la_charito.dtos.response.lote.ModificacionLoteResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.LoteControlResponse;
import com.tesis.queseria_la_charito.dtos.response.lote.LoteResponse;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.LoteEntity;
import com.tesis.queseria_la_charito.entities.ModificacionLoteEntity;
import com.tesis.queseria_la_charito.entities.usuario.UsuarioEntity;
import com.tesis.queseria_la_charito.models.Estado;
import com.tesis.queseria_la_charito.models.Quesos;
import com.tesis.queseria_la_charito.models.TipoItem;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.LoteRepository;
import com.tesis.queseria_la_charito.repositories.ModificacionLoteRepository;
import com.tesis.queseria_la_charito.repositories.usuario.UsuarioRepository;
import com.tesis.queseria_la_charito.services.LoteService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoteServiceImpl implements LoteService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private LoteRepository loteRepository;

    @Autowired
    private ModificacionLoteRepository modificacionLotesRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ModificacionLoteRepository modificacionesLotesRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<LoteResponse> getAll(Long idItem, String estado) {
        Optional<ItemEntity> itemEntityOptional = itemRepository.findById(idItem);
        if(itemEntityOptional.isEmpty()){
            throw new EntityNotFoundException("No se encontró el item");
        }

        List<LoteResponse> listaLotesResponse = new ArrayList<>();
        List<LoteEntity> listaLotesEntity = loteRepository.findByItemAndEstadoAndMostrar(itemEntityOptional.get(), estado, true);
        if (listaLotesEntity.isEmpty()) {
            return new ArrayList<>();
        }

        for (LoteEntity loteEntity : listaLotesEntity) {
            listaLotesResponse.add(modelMapper.map(loteEntity, LoteResponse.class));
        }

        return listaLotesResponse;
    }

    @Override
    public LoteResponse getLoteById(String id) {
        Optional<LoteEntity> loteEntityOptional = loteRepository.findById(id);
        if (loteEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró un lote con ese código");
        }

        return modelMapper.map(loteEntityOptional.get(), LoteResponse.class);
    }

    @Override
    public LoteResponse postLote(Long id_item, Integer unidades) {
        LoteEntity loteEntity = new LoteEntity();

        Optional<ItemEntity> itemEntityOptional = itemRepository.findById(id_item);
        if (itemEntityOptional.isEmpty()){
            throw new EntityNotFoundException("No se encontró el item");
        }
        ItemEntity itemEntity = itemEntityOptional.get();

        loteEntity.setItem(itemEntity);
        loteEntity.setUnidades(unidades);
        loteEntity.setMostrar(true);
        String inicial;
        if(Objects.equals(itemEntity.getTipo(), TipoItem.Insumo.name())){
            loteEntity.setEstado(Estado.Disponible.name());
            inicial = "I";
        } else {
            loteEntity.setEstado(Estado.Elaborando.name());
            inicial = "Q";
        }

        String inicialItem = loteEntity.getItem().getNombre().substring(0, 1).toUpperCase();
        String cantidadLotes = String.valueOf(loteRepository.findAll().size());

        loteEntity.setId(inicial + inicialItem + cantidadLotes);

        return modelMapper.map(loteRepository.save(loteEntity), LoteResponse.class);
    }

    @Override
    public LoteResponse putLote(LoteRequest lote, String id) {
        ModificacionLoteEntity modificacionesLotesEntity = new ModificacionLoteEntity();

        Optional<LoteEntity> loteEntityOptional = loteRepository.findById(id);
        if(loteEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró ningún lote");
        }

        
        LoteEntity loteEntity = loteEntityOptional.get();

        modificacionesLotesEntity.setMotivo(lote.getMotivos());
        modificacionesLotesEntity.setCantidadPrevia(loteEntity.getUnidades());
        modificacionesLotesEntity.setFecha(lote.getFecha());
        modificacionesLotesEntity.setNuevo(true);

        Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findByUsername(lote.getUsuario());
        if (usuarioEntity.isEmpty()) {
            throw new EntityNotFoundException("No se ha encontrado el usuario");
        }
        modificacionesLotesEntity.setUsuario(usuarioEntity.get());

        loteEntity.setUnidades(lote.getUnidades());
        if (loteEntity.getUnidades() == 0) {
            loteEntity.setEstado(Estado.Despachado.name());
        } else if (loteEntity.getEstado().equals(Estado.Despachado.name())){
            loteEntity.setEstado(Estado.Terminado.name());
        }

        modificacionesLotesEntity.setCantidadPosterior(loteEntity.getUnidades());
        modificacionesLotesEntity.setLote(loteEntity);

        modificacionesLotesRepository.save(modificacionesLotesEntity);
        return modelMapper.map(loteRepository.save(loteEntity), LoteResponse.class);
    }

    @Override
    public LoteResponse deleteLote(String id) {
        Optional<LoteEntity> loteEntityOptional = loteRepository.findById(id);
        if (loteEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el lote");
        }

        LoteEntity loteEntity = loteEntityOptional.get();

        if (loteEntity.getUnidades() > 0 && loteEntity.getElaboracion() != null) {
            throw new IllegalStateException("No se puede eliminar el lote porque aún contiene unidades.");
        }

        try{
            loteEntity.setMostrar(false);
            return modelMapper.map(loteRepository.save(loteEntity), LoteResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<LoteControlResponse> getUnidades(String item) {
        List<LoteControlResponse> lotes = new ArrayList<>();

        List<LoteEntity> lotesEntities = new ArrayList<>();

        if(item == null) {
            lotesEntities = loteRepository.findAll();
            if (lotesEntities.isEmpty()) {
                return new ArrayList<>();
            }
        } else {
            Optional<ItemEntity> itemEntity = itemRepository.findByNombre(item);
            if (itemEntity.isEmpty()) {
                throw new EntityNotFoundException("No se ha encontrado el item");
            }

            lotesEntities = loteRepository.findByItem(itemEntity.get());
            if (lotesEntities.isEmpty()) {
                return new ArrayList<>();
            }
        }


        for (LoteEntity loteEntity: lotesEntities) {
            LoteControlResponse lote = new LoteControlResponse();
            lote.setUnidades(loteEntity.getUnidades());
            lote.setItem(loteEntity.getItem().getNombre());
            lote.setId(loteEntity.getId());

            if (lote.getItem().equals(Quesos.Pategras.name()) || lote.getItem().equals(Quesos.Cremoso.name()) ||lote.getItem().equals(Quesos.Barra.name())) {
                if (loteEntity.getElaboracion() != null) {
                    if (loteEntity.getElaboracion().getDetalleCorte() != null) {
                        lote.setCorte(loteEntity.getElaboracion().getDetalleCorte().getCorte());
                    }
                }
            }

            lotes.add(lote);
        }

        return lotes;
    }

    @Override
    public List<ModificacionLoteResponse> getModificaciones(boolean validate) {
        List<ModificacionLoteEntity> modificacionesLotesEntityList = modificacionLotesRepository.findAllByOrderByFechaDescIdDesc();
        if (modificacionesLotesEntityList.isEmpty()) {
            return new ArrayList<>();
        }

        List<ModificacionLoteResponse> modificacionesLotesResponses = new ArrayList<>();

        for (ModificacionLoteEntity modificacionesLotesEntity : modificacionesLotesEntityList) {
            modificacionesLotesResponses.add(modelMapper.map(modificacionesLotesEntity, ModificacionLoteResponse.class));

            if (!validate) {
                modificacionesLotesEntity.setNuevo(false);
                modificacionLotesRepository.save(modificacionesLotesEntity);
            }
        }

        return modificacionesLotesResponses;
    }
}
