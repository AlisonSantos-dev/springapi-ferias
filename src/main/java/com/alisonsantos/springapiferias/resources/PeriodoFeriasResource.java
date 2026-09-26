package com.alisonsantos.springapiferias.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alisonsantos.springapiferias.dtos.PeriodoFeriasDTO;
import com.alisonsantos.springapiferias.dtos.PeriodoFeriasInsertDTO;
import com.alisonsantos.springapiferias.services.PeriodoFeriasService;

@RestController
@RequestMapping("/api/ferias")
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

    // visibilidade aberta a qualquer colaborador autenticado - ver a agenda
    // do time ajuda a evitar pedir ferias em cima de um colega (a validacao
    // de conflito e so um aviso, entao a transparencia aqui importa)
    @GetMapping("/equipe/{equipeId}")
    public ResponseEntity<List<PeriodoFeriasDTO>> porEquipe(@PathVariable Long equipeId) {
        return ResponseEntity.ok(service.porEquipe(equipeId));
    }

    // visao geral: todas as equipes juntas (calendario e lista da tela do colaborador)
    @GetMapping("/todos")
    public ResponseEntity<List<PeriodoFeriasDTO>> todos() {
        return ResponseEntity.ok(service.todosOsPeriodos());
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

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        service.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
