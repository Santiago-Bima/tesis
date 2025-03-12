package com.tesis.queseria_la_charito.controllers.formulas;

import com.tesis.queseria_la_charito.dtos.request.ItemRequest;
import com.tesis.queseria_la_charito.dtos.response.ItemResponse;
import com.tesis.queseria_la_charito.services.formulas.InsumosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("insumos")
public class SupplyController {
    @Autowired
    private InsumosService service;


    @PreAuthorize("hasAnyRole('ROLE_Subgerente', 'ROLE_Operario'")
    @GetMapping("")
    public List<ItemResponse> getAll() { return service.getItems(); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("/{id}")
    public ItemResponse getById(@PathVariable final Long id) { return service.getItemById(id); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @PostMapping("")
    public ItemResponse post(@RequestBody final ItemRequest item) throws Exception { return service.postItem(item); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @DeleteMapping("/{id}")
    public ItemResponse delete(@PathVariable final Long id) { return service.deleteItem(id); }

    @PreAuthorize("hasRole('ROLE_Subgerente'")
    @PutMapping("/{id}")
    public  ItemResponse put(@RequestBody final ItemRequest item, @PathVariable Long id) { return service.putItem(item, id); }
}
