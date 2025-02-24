package com.tesis.queseria_la_charito.services;

import com.tesis.queseria_la_charito.dtos.response.formula.TipoQuesoResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ProductoService {
    List<TipoQuesoResponse> getAll();
    TipoQuesoResponse getById(Long id);
}
