package com.tesis.queseria_la_charito.controllers.formula;

import com.tesis.queseria_la_charito.dtos.request.ItemRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import com.tesis.queseria_la_charito.services.impls.InsumosServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("insumos")
public class InsumoController {
    @Autowired
    private InsumosServiceImpl insumosService;


    @PreAuthorize("hasAnyRole('ROLE_Subgerente', 'ROLE_Operario'")
    @GetMapping("")
    public List<ItemResponse> getInsumos() { return insumosService.getItems(); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("/{id}")
    public ItemResponse getInsumo(@PathVariable final Long id) { return insumosService.getItemById(id); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @PostMapping("")
    public ItemResponse postInsumo(@RequestBody final ItemRequest item) throws Exception { return insumosService.postItem(item); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @DeleteMapping("/{id}")
    public ItemResponse deleteInsumo(@PathVariable final Long id) { return insumosService.deleteItem(id); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @PutMapping("/{id}")
    public  ItemResponse putInsumo(@RequestBody final ItemRequest item, @PathVariable Long id) { return insumosService.putItem(item, id); }
}
