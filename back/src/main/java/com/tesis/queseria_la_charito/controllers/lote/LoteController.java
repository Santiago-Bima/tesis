package com.tesis.queseria_la_charito.controllers.lote;

import com.tesis.queseria_la_charito.dtos.request.LoteRequest;
import com.tesis.queseria_la_charito.dtos.response.lote.ModificacionLoteResponse;
import com.tesis.queseria_la_charito.dtos.response.controlStock.LoteControlResponse;
import com.tesis.queseria_la_charito.dtos.response.lote.LoteResponse;
import com.tesis.queseria_la_charito.services.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("lotes")
public class LoteController {
    @Autowired
    private LoteService service;

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("/{idItem}/{estado}")
    public List<LoteResponse> getAll(@PathVariable Long idItem, @PathVariable String estado) { return service.getAll(idItem, estado); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("/{id}")
    public LoteResponse getById(@PathVariable String id) { return service.getLoteById(id); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @PutMapping("/{id}")
    public LoteResponse put(@RequestBody LoteRequest lote, @PathVariable String id) { return service.putLote(lote, id); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @DeleteMapping("/{id}")
    public LoteResponse delete(@PathVariable String id) { return service.deleteLote(id); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @GetMapping("/controlStock/{item}")
    public List<LoteControlResponse> getUnidades(@PathVariable String item) { return service.getUnidades(item); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @GetMapping("/modificaciones")
    public List<ModificacionLoteResponse> getModificaciones() { return service.getModificaciones(false); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @GetMapping("/modificaciones/validate")
    public List<ModificacionLoteResponse> getModificacionesValidate() { return service.getModificaciones(true); }
}
