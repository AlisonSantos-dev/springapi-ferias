package com.alisonsantos.springapiferias.services;

import com.alisonsantos.springapiferias.dtos.PeriodoFeriasDTO;
import com.alisonsantos.springapiferias.dtos.PeriodoFeriasInsertDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ferias")
public class PeriodoFeriasResource {

    @Autowired
    private PeriodoFeriasService service;

    @PostMapping
    public ResponseEntity<PeriodoFeriasDTO> solicitar(@RequestBody PeriodoFeriasInsertDTO dto) {
        return ResponseEntity.ok(service.solicitar(dto));
    }

    @GetMapping("/meus")
    public ResponseEntity<List<PeriodoFeriasDTO>> meusPeriodos() {
        return ResponseEntity.ok(service.meusPeriodos());
    }

    @GetMapping("/equipe/{equipeId}")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<List<PeriodoFeriasDTO>> porEquipe(@PathVariable Long equipeId) {
        return ResponseEntity.ok(service.porEquipe(equipeId));
    }

    @PatchMapping("/{id}/aprovar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<PeriodoFeriasDTO> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(service.aprovar(id));
    }

    @PatchMapping("/{id}/rejeitar")
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<PeriodoFeriasDTO> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(service.rejeitar(id));
    }

}
