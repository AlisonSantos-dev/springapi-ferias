package com.alisonsantos.springapiferias.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alisonsantos.springapiferias.dtos.ColaboradorDTO;
import com.alisonsantos.springapiferias.dtos.ColaboradorInsertDTO;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.entities.Equipe;
import com.alisonsantos.springapiferias.entities.enums.Role;
import com.alisonsantos.springapiferias.repositories.ColaboradorRepository;
import com.alisonsantos.springapiferias.repositories.EquipeRepository;
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;

@Service
public class ColaboradorService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<ColaboradorDTO> findAll() {
        return colaboradorRepository.findAll().stream().map(ColaboradorDTO::new).collect(Collectors.toList());
    }

    public ColaboradorDTO insert(ColaboradorInsertDTO dto) {
        validar(dto);

        Equipe equipe = equipeRepository.findById(dto.getEquipeId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.getEquipeId()));

        Role role;
        try {
            role = Role.valueOf(dto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("role invalida: use COLABORADOR ou COORDENADOR");
        }

        Colaborador entity = new Colaborador(null, dto.getNome(), dto.getEmail(),
                passwordEncoder.encode(dto.getSenha()), role, equipe);

        entity = colaboradorRepository.save(entity);
        return new ColaboradorDTO(entity);
    }

    private void validar(ColaboradorInsertDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
        if (dto.getSenha() == null || dto.getSenha().length() < 6) {
            throw new IllegalArgumentException("senha e obrigatoria e deve ter pelo menos 6 caracteres");
        }
        if (dto.getRole() == null || dto.getRole().isBlank()) {
            throw new IllegalArgumentException("role e obrigatoria (COLABORADOR ou COORDENADOR)");
        }
        if (dto.getEquipeId() == null) {
            throw new IllegalArgumentException("equipeId e obrigatorio");
        }
        colaboradorRepository.findByEmail(dto.getEmail()).ifPresent(c -> {
            throw new IllegalArgumentException("ja existe um colaborador com esse email");
        });
    }
}
