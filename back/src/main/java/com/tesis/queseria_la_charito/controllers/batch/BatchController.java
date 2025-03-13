package com.tesis.queseria_la_charito.controllers.batch;

import com.tesis.queseria_la_charito.dtos.request.BatchRequest;
import com.tesis.queseria_la_charito.dtos.response.batch.BatchModificationResponse;
import com.tesis.queseria_la_charito.dtos.response.stockControl.BatchControlResponse;
import com.tesis.queseria_la_charito.dtos.response.batch.BatchResponse;
import com.tesis.queseria_la_charito.services.lotes.LoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("lotes")
public class BatchController {
    @Autowired
    private LoteService service;

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("/{idItem}/{estado}")
    public List<BatchResponse> getAll(@PathVariable Long idItem, @PathVariable String estado) { return service.getAll(idItem, estado); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("/{id}")
    public BatchResponse getById(@PathVariable String id) { return service.getLoteById(id); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @PutMapping("/{id}")
    public BatchResponse put(@RequestBody BatchRequest lote, @PathVariable String id) { return service.putLote(lote, id); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @DeleteMapping("/{id}")
    public BatchResponse delete(@PathVariable String id) { return service.deleteLote(id); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @GetMapping("/controlStock/{item}")
    public List<BatchControlResponse> getQuantity(@PathVariable String item) { return service.getUnidades(item); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @GetMapping("/modificaciones")
    public List<BatchModificationResponse> getModifications() { return service.getModificaciones(false); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @GetMapping("/modificaciones/validate")
    public List<BatchModificationResponse> getModificationsValidate() { return service.getModificaciones(true); }
}
