package com.tesis.queseria_la_charito.controllers.batch;

import com.tesis.queseria_la_charito.models.Status;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/estados")
public class StatusController {
    @PreAuthorize("hasRole('ROLE_Operario'")
    @GetMapping()
    List<Status> get() { return Arrays.asList(Status.values()) ;}
}
