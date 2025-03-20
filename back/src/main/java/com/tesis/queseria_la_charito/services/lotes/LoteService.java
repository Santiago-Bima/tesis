package com.tesis.queseria_la_charito.services.lotes;

import com.tesis.queseria_la_charito.dtos.request.BatchRequest;
import com.tesis.queseria_la_charito.dtos.response.batch.BatchModificationResponse;
import com.tesis.queseria_la_charito.dtos.response.stockControl.BatchControlResponse;
import com.tesis.queseria_la_charito.dtos.response.batch.BatchResponse;
import com.tesis.queseria_la_charito.entities.batch.BatchEntity;
import com.tesis.queseria_la_charito.entities.ItemEntity;
import com.tesis.queseria_la_charito.entities.batch.BatchModificationEntity;
import com.tesis.queseria_la_charito.entities.user.UserEntity;
import com.tesis.queseria_la_charito.models.Status;
import com.tesis.queseria_la_charito.models.Cheese;
import com.tesis.queseria_la_charito.models.ItemType;
import com.tesis.queseria_la_charito.repositories.ItemRepository;
import com.tesis.queseria_la_charito.repositories.batch.BatchRepository;
import com.tesis.queseria_la_charito.repositories.batch.BatchModificationRepository;
import com.tesis.queseria_la_charito.repositories.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class LoteService {
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BatchRepository batchRepository;

    @Autowired
    private BatchModificationRepository modificacionLotesRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BatchModificationRepository modificacionesLotesRepository;

    @Autowired
    private UserRepository usuarioRepository;


    public List<BatchResponse> getAll(Long idItem, String estado) {
        Optional<ItemEntity> itemEntityOptional = itemRepository.findById(idItem);
        if(itemEntityOptional.isEmpty()){
            throw new EntityNotFoundException("No se encontró el item");
        }

        List<BatchResponse> listaLotesResponse = new ArrayList<>();
        List<BatchEntity>   listaLotesEntity   = batchRepository.findByItemAndEstadoAndMostrar(itemEntityOptional.get(), estado, true);
        if (listaLotesEntity.isEmpty()) {
            return new ArrayList<>();
        }

        for (BatchEntity batchEntity : listaLotesEntity) {
            listaLotesResponse.add(modelMapper.map(batchEntity, BatchResponse.class));
        }

        return listaLotesResponse;
    }

    public BatchResponse getLoteById(String id) {
        Optional<BatchEntity> loteEntityOptional = batchRepository.findById(id);
        if (loteEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró un lote con ese código");
        }

        return modelMapper.map(loteEntityOptional.get(), BatchResponse.class);
    }

    public BatchResponse postLote(Long id_item, Integer unidades) {
        BatchEntity batchEntity = new BatchEntity();

        Optional<ItemEntity> itemEntityOptional = itemRepository.findById(id_item);
        if (itemEntityOptional.isEmpty()){
            throw new EntityNotFoundException("No se encontró el item");
        }
        ItemEntity itemEntity = itemEntityOptional.get();

        batchEntity.setItem(itemEntity);
        batchEntity.setUnidades(unidades);
        batchEntity.setMostrar(true);
        String inicial;
        if(Objects.equals(itemEntity.getTipo(), ItemType.Insumo.name())){
            batchEntity.setEstado(Status.Disponible.name());
            inicial = "I";
        } else {
            batchEntity.setEstado(Status.Elaborando.name());
            inicial = "Q";
        }

        String inicialItem = batchEntity.getItem().getNombre().substring(0, 1).toUpperCase();
        String cantidadLotes = String.valueOf(batchRepository.findAll().size());

        batchEntity.setId(inicial + inicialItem + cantidadLotes);

        return modelMapper.map(batchRepository.save(batchEntity), BatchResponse.class);
    }

    public BatchResponse putLote(BatchRequest lote, String id) {
        BatchModificationEntity modificacionesLotesEntity = new BatchModificationEntity();

        Optional<BatchEntity> loteEntityOptional = batchRepository.findById(id);
        if(loteEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró ningún lote");
        }

        
        BatchEntity batchEntity = loteEntityOptional.get();

        modificacionesLotesEntity.setMotivo(lote.getMotivos());
        modificacionesLotesEntity.setCantidadPrevia(batchEntity.getUnidades());
        modificacionesLotesEntity.setFecha(lote.getFecha());
        modificacionesLotesEntity.setNuevo(true);

        Optional<UserEntity> usuarioEntity = usuarioRepository.findByUsername(lote.getUsuario());
        if (usuarioEntity.isEmpty()) {
            throw new EntityNotFoundException("No se ha encontrado el usuario");
        }
        modificacionesLotesEntity.setUsuario(usuarioEntity.get());

        batchEntity.setUnidades(lote.getUnidades());
        if (batchEntity.getUnidades() == 0) {
            batchEntity.setEstado(Status.Despachado.name());
        } else if (batchEntity.getEstado().equals(Status.Despachado.name())){
            batchEntity.setEstado(Status.Terminado.name());
        }

        modificacionesLotesEntity.setCantidadPosterior(batchEntity.getUnidades());
        modificacionesLotesEntity.setLote(batchEntity);

        modificacionesLotesRepository.save(modificacionesLotesEntity);
        return modelMapper.map(batchRepository.save(batchEntity), BatchResponse.class);
    }

    public BatchResponse deleteLote(String id) {
        Optional<BatchEntity> loteEntityOptional = batchRepository.findById(id);
        if (loteEntityOptional.isEmpty()) {
            throw new EntityNotFoundException("No se encontró el lote");
        }

        BatchEntity batchEntity = loteEntityOptional.get();

        if (batchEntity.getUnidades() > 0 && batchEntity.getElaboracion() != null) {
            throw new IllegalStateException("No se puede eliminar el lote porque aún contiene unidades.");
        }

        try{
            batchEntity.setMostrar(false);
            return modelMapper.map(batchRepository.save(batchEntity), BatchResponse.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<BatchControlResponse> getUnidades(String item) {
        List<BatchControlResponse> lotes = new ArrayList<>();

        List<BatchEntity> lotesEntities = new ArrayList<>();

        if(item == null) {
            lotesEntities = batchRepository.findAll();
            if (lotesEntities.isEmpty()) {
                return new ArrayList<>();
            }
        } else {
            Optional<ItemEntity> itemEntity = itemRepository.findByNombre(item);
            if (itemEntity.isEmpty()) {
                throw new EntityNotFoundException("No se ha encontrado el item");
            }

            lotesEntities = batchRepository.findByItem(itemEntity.get());
            if (lotesEntities.isEmpty()) {
                return new ArrayList<>();
            }
        }


        for (BatchEntity batchEntity : lotesEntities) {
            BatchControlResponse lote = new BatchControlResponse();
            lote.setUnidades(batchEntity.getUnidades());
            lote.setItem(batchEntity.getItem().getNombre());
            lote.setId(batchEntity.getId());

            if (lote.getItem().equals(Cheese.Pategras.name()) || lote.getItem().equals(Cheese.Cremoso.name()) || lote.getItem().equals(Cheese.Barra.name())) {
                if (batchEntity.getElaboracion() != null) {
                    if (batchEntity.getElaboracion().getDetalleCorte() != null) {
                        lote.setCorte(batchEntity.getElaboracion().getDetalleCorte().getCorte());
                    }
                }
            }

            lotes.add(lote);
        }

        return lotes;
    }

    public List<BatchModificationResponse> getModificaciones(boolean validate) {
        List<BatchModificationEntity> modificacionesLotesEntityList = modificacionLotesRepository.findAllByOrderByFechaDescIdDesc();
        if (modificacionesLotesEntityList.isEmpty()) {
            return new ArrayList<>();
        }

        List<BatchModificationResponse> modificacionesLotesResponses = new ArrayList<>();

        for (BatchModificationEntity modificacionesLotesEntity : modificacionesLotesEntityList) {
            modificacionesLotesResponses.add(modelMapper.map(modificacionesLotesEntity, BatchModificationResponse.class));

            if (!validate) {
                modificacionesLotesEntity.setNuevo(false);
                modificacionLotesRepository.save(modificacionesLotesEntity);
            }
        }

        return modificacionesLotesResponses;
    }
}
