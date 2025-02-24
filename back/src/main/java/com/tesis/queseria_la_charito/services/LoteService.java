package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.request.LoteRequest;
import com.tesis.queseria_la_charito.dtos.response.lote.ModificacionLoteResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.LoteControlResponse;
import com.tesis.queseria_la_charito.dtos.response.lote.LoteResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LoteService {
    List<LoteResponse> getAll(Long idItem, String estado);
    LoteResponse getLoteById(String id);
    LoteResponse postLote(Long item_id, Integer unidades);
    LoteResponse putLote(LoteRequest lote, String id);
    LoteResponse deleteLote(String id);
    List<LoteControlResponse> getUnidades(String item);
    List<ModificacionLoteResponse> getModificaciones(boolean validate);
}
