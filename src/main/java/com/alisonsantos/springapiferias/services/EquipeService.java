package com.alisonsantos.springapiferias.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alisonsantos.springapiferias.dtos.EquipeDTO;
import com.alisonsantos.springapiferias.dtos.EquipeInsertDTO;
import com.alisonsantos.springapiferias.entities.Equipe;
import com.alisonsantos.springapiferias.repositories.EquipeRepository;
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;

@Service
public class EquipeService {

    @Autowired
    private EquipeRepository equipeRepository;

    public List<EquipeDTO> findAll() {
        return equipeRepository.findAll().stream().map(EquipeDTO::new).collect(Collectors.toList());
    }

    public EquipeDTO findById(Long id) {
        Equipe entity = equipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
        return new EquipeDTO(entity);
    }

    public EquipeDTO insert(EquipeInsertDTO dto) {
        if (dto.getNome() == null || dto.getNome().isBlank()) {
            throw new IllegalArgumentException("nome da equipe e obrigatorio");
        }
        Equipe entity = new Equipe(null, dto.getNome());
        entity = equipeRepository.save(entity);
        return new EquipeDTO(entity);
    }
}
