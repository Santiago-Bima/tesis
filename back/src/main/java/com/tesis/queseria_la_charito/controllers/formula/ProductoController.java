package com.tesis.queseria_la_charito.controllers.formula;

import com.tesis.queseria_la_charito.dtos.response.formula.TipoQuesoResponse;
import com.tesis.queseria_la_charito.services.impls.ProductosServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("productos")
public class ProductoController {
    @Autowired
    private ProductosServiceImpl productosService;

    @GetMapping("")
    public List<TipoQuesoResponse> getAll() { return productosService.getAll(); }

    @GetMapping("/{id}")
    public TipoQuesoResponse getById(@PathVariable Long id) { return productosService.getById(id); }
}
