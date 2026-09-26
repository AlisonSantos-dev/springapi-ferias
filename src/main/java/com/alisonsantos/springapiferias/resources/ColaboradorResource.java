package com.alisonsantos.springapiferias.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alisonsantos.springapiferias.dtos.ColaboradorCadastroDTO;
import com.alisonsantos.springapiferias.dtos.ColaboradorDTO;
import com.alisonsantos.springapiferias.dtos.ColaboradorInsertDTO;
import com.alisonsantos.springapiferias.dtos.TrocarSenhaDTO;
import com.alisonsantos.springapiferias.services.ColaboradorService;

@RestController
@RequestMapping("/api/colaboradores")
public class ColaboradorResource {

    @Autowired
    private ColaboradorService service;

    // qualquer colaborador autenticado pode ver a lista (ex.: pra saber quem e o time)
    @GetMapping
    public ResponseEntity<List<ColaboradorDTO>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // so coordenador cadastra um colaborador novo (e pode escolher a role, inclusive COORDENADOR)
    @PostMapping
    @PreAuthorize("hasRole('COORDENADOR')")
    public ResponseEntity<ColaboradorDTO> insert(@RequestBody ColaboradorInsertDTO dto) {
        return ResponseEntity.ok(service.insert(dto));
    }

    // cadastro publico: qualquer pessoa pode se cadastrar sozinha, sempre como COLABORADOR
    @PostMapping("/cadastro")
    public ResponseEntity<ColaboradorDTO> cadastro(@RequestBody ColaboradorCadastroDTO dto) {
        return ResponseEntity.ok(service.cadastrarPublico(dto));
    }

    // qualquer colaborador autenticado troca a propria senha
    @PatchMapping("/senha")
    public ResponseEntity<Void> trocarSenha(@RequestBody TrocarSenhaDTO dto) {
        service.trocarSenha(dto);
        return ResponseEntity.noContent().build();
    }
}
