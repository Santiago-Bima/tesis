package com.tesis.queseria_la_charito.controllers.production;

import com.tesis.queseria_la_charito.dtos.request.production.ProductionRequest;
import com.tesis.queseria_la_charito.dtos.request.production.QualityControlRequest;
import com.tesis.queseria_la_charito.dtos.request.production.CutDetailRequest;
import com.tesis.queseria_la_charito.dtos.request.production.MadurationRequest;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.ElaboracionResponse;
import com.tesis.queseria_la_charito.dtos.response.elaboracion.InformeElaboracionResponse;
import com.tesis.queseria_la_charito.services.elaboraciones.ElaboracionesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/elaboraciones")
public class ProductionController {
  @Autowired
  private ElaboracionesService service;


  @PreAuthorize("hasRole('ROLE_Operario'")
  @GetMapping("/mis-elaboraciones/{username}")
  List<ElaboracionResponse> getAll(@PathVariable String username, @RequestParam(required = false) LocalDate fechaInicio, @RequestParam(required = false) LocalDate fechaFin, @RequestParam Long productId) { return service.getAll(username, fechaInicio, fechaFin, productId); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @GetMapping("/mis-elaboraciones/{username}/{id}")
  ElaboracionResponse getById(@PathVariable String username, @PathVariable String id){ return service.getById(username, id); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PostMapping("")
  ElaboracionResponse post(@RequestBody ProductionRequest elaboracionRequest) { return  service.post(elaboracionRequest); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PutMapping("/cortes/{id}")
  ElaboracionResponse updateCuts(@RequestBody CutDetailRequest detalleCorteRequest , @PathVariable String id) throws Exception { return service.updateCortes(detalleCorteRequest, id); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PutMapping("/embolsado/{id}")
  ElaboracionResponse updatePackagingDate(@RequestParam LocalDate fechaEmbolsado , @PathVariable String id) throws Exception { return service.updateEmbolsado(fechaEmbolsado, id); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PutMapping("/pintado/{id}")
  ElaboracionResponse updatePaintingDate(@RequestParam LocalDate fechaPintado , @PathVariable String id) throws Exception { return service.updatePintado(fechaPintado, id); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PutMapping("/maduracion/{id}")
  ElaboracionResponse updateMaturation(@RequestBody MadurationRequest maduracionRequest , @PathVariable String id) throws Exception { return service.updateMaduracion(maduracionRequest, id); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @PutMapping("/controles/{id}")
  ElaboracionResponse updateQualityControl(@RequestBody QualityControlRequest controlCalidadRequest , @PathVariable String id) throws Exception { return service.updateControl(controlCalidadRequest, id); }

  @PreAuthorize("hasRole('ROLE_Operario'")
  @DeleteMapping("/{id}")
  ElaboracionResponse delete(@PathVariable String id) { return service.deleteElaboracion(id); }

  @PreAuthorize("hasRole('ROLE_Gerente'")
  @GetMapping("/informes")
  InformeElaboracionResponse generateReport(@RequestParam LocalDate fechaInicio, @RequestParam LocalDate fechaFin) { return service.generateInforme(fechaInicio, fechaFin); }
}
