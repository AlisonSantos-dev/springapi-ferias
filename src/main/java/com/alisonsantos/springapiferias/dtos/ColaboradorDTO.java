package com.alisonsantos.springapiferias.dtos;

import com.alisonsantos.springapiferias.entities.Colaborador;

public class ColaboradorDTO {

    private Long id;
    private String nome;
    private String email;
    private String role;
    private Long equipeId;
    private String equipeNome;

    public ColaboradorDTO() {
    }

    public ColaboradorDTO(Colaborador entity) {
        this.id = entity.getId();
        this.nome = entity.getNome();
        this.email = entity.getEmail();
        this.role = entity.getRole().name();
        if (entity.getEquipe() != null) {
            this.equipeId = entity.getEquipe().getId();
            this.equipeNome = entity.getEquipe().getNome();
        }
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }

    public Long getEquipeId() {
        return equipeId;
    }

    public String getEquipeNome() {
        return equipeNome;
    }
}
