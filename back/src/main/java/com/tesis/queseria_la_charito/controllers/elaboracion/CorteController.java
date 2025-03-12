package com.tesis.queseria_la_charito.controllers.elaboracion;

import com.tesis.queseria_la_charito.models.TipoCorte;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/cortes")
public class CorteController {
    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping()
    List<TipoCorte> getCortes() { return Arrays.asList(TipoCorte.values()); }
}
