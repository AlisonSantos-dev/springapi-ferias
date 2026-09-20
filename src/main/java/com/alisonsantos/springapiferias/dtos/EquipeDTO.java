package com.alisonsantos.springapiferias.dtos;

import com.alisonsantos.springapiferias.entities.Equipe;

public class EquipeDTO {

    private Long id;
    private String nome;

    public EquipeDTO() {
    }

    public EquipeDTO(Equipe entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}
