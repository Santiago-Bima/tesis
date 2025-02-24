package com.tesis.queseria_la_charito.controllers.elaboracion;

import com.tesis.queseria_la_charito.dtos.request.ElaboracionRequest;
import com.tesis.queseria_la_charito.dtos.request.procesosElaboracion.ControlCalidadRequest;
import com.tesis.queseria_la_charito.dtos.request.procesosElaboracion.DetalleCorteRequest;
import com.tesis.queseria_la_charito.dtos.request.procesosElaboracion.MaduracionRequest;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.ElaboracionResponse;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.InformeElaboracionResponse;
import com.tesis.queseria_la_charito.services.ElaboracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/elaboraciones")
public class ElaboracionController {
  @Autowired
  private ElaboracionService elaboracionesService;

  @GetMapping("/mis-elaboraciones/{username}")
  List<ElaboracionResponse> getAll(@PathVariable String username, @RequestParam(required = false) LocalDate fechaInicio, @RequestParam(required = false) LocalDate fechaFin, @RequestParam Long productId) { return elaboracionesService.getAll(username, fechaInicio, fechaFin, productId); }

  @GetMapping("/mis-elaboraciones/{username}/{id}")
  ElaboracionResponse getById(@PathVariable String username, @PathVariable String id){ return elaboracionesService.getById(username, id); }

  @PostMapping("")
  ElaboracionResponse post(@RequestBody ElaboracionRequest elaboracionRequest) { return  elaboracionesService.post(elaboracionRequest); }

  @PutMapping("/cortes/{id}")
  ElaboracionResponse updateCortes(@RequestBody DetalleCorteRequest detalleCorteRequest , @PathVariable String id) throws Exception { return elaboracionesService.updateCortes(detalleCorteRequest, id); }

  @PutMapping("/embolsado/{id}")
  ElaboracionResponse updateEmbolsado(@RequestParam LocalDate fechaEmbolsado , @PathVariable String id) throws Exception { return elaboracionesService.updateEmbolsado(fechaEmbolsado, id); }

  @PutMapping("/pintado/{id}")
  ElaboracionResponse updatePintado(@RequestParam LocalDate fechaPintado , @PathVariable String id) throws Exception { return elaboracionesService.updatePintado(fechaPintado, id); }

  @PutMapping("/maduracion/{id}")
  ElaboracionResponse updateMaduracion(@RequestBody MaduracionRequest maduracionRequest , @PathVariable String id) throws Exception { return elaboracionesService.updateMaduracion(maduracionRequest, id); }

  @PutMapping("/controles/{id}")
  ElaboracionResponse updateControlesCalidad(@RequestBody ControlCalidadRequest controlCalidadRequest , @PathVariable String id) throws Exception { return elaboracionesService.updateControl(controlCalidadRequest, id); }

  @DeleteMapping("/{id}")
  ElaboracionResponse delete(@PathVariable String id) { return elaboracionesService.deleteElaboracion(id); }

  @GetMapping("/informes")
  InformeElaboracionResponse generateInfome(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) { return elaboracionesService.generateInforme(fechaInicio, fechaFin); }
}
