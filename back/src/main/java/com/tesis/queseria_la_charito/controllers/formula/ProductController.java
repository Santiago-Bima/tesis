package com.tesis.queseria_la_charito.controllers.formula;

import com.tesis.queseria_la_charito.dtos.response.formula.TipoQuesoResponse;
import com.tesis.queseria_la_charito.services.formulas.ProductosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("productos")
public class ProductController {
    @Autowired
    private ProductosService productosService;

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("")
    public List<TipoQuesoResponse> getAll() { return productosService.getAll(); }

    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping("/{id}")
    public TipoQuesoResponse getById(@PathVariable Long id) { return productosService.getById(id); }
}
