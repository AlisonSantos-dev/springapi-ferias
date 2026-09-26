package com.alisonsantos.springapiferias.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alisonsantos.springapiferias.dtos.EquipeDTO;
import com.alisonsantos.springapiferias.dtos.EquipeInsertDTO;
import com.alisonsantos.springapiferias.services.EquipeService;

@RestController
@RequestMapping("/api/equipes")
public class EquipeResource {

    @Autowired
    private EquipeService service;

    // qualquer colaborador autenticado pode ver as equipes existentes
    @GetMapping
    public ResponseEntity<List<EquipeDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EquipeDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // so coordenador cria uma equipe nova
    @PostMapping
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<EquipeDTO> insert(@RequestBody EquipeInsertDTO dto) {
        return ResponseEntity.ok(service.insert(dto));
    }
}
