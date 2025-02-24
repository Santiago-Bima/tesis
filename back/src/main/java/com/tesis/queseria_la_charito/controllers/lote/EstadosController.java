package com.tesis.queseria_la_charito.controllers.lote;

import com.tesis.queseria_la_charito.models.Estado;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/estados")
public class EstadosController {
    @GetMapping()
    List<Estado> get() { return Arrays.asList(Estado.values()) ;}
}
