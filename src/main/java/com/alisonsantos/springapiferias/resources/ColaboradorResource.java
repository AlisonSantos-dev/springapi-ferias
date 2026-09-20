package com.alisonsantos.springapiferias.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alisonsantos.springapiferias.dtos.ColaboradorDTO;
import com.alisonsantos.springapiferias.dtos.ColaboradorInsertDTO;
import com.alisonsantos.springapiferias.services.ColaboradorService;

@RestController
@RequestMapping("/colaboradores")
public class ColaboradorResource {

    @Autowired
    private ColaboradorService service;

    // qualquer colaborador autenticado pode ver a lista (ex.: pra saber quem e o time)
    @GetMapping
    public ResponseEntity<List<ColaboradorDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // so coordenador cadastra um colaborador novo
    @PostMapping
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ColaboradorDTO> insert(@RequestBody ColaboradorInsertDTO dto) {
        return ResponseEntity.ok(service.insert(dto));
    }
}
