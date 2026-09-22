package com.alisonsantos.springapiferias.dtos;

import com.alisonsantos.springapiferias.entities.PeriodoFerias;
import com.alisonsantos.springapiferias.entities.enums.StatusFerias;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

public class PeriodoFeriasDTO {


    private Long id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusFerias status;
    private String solicitanteNome;
    private String equipeNome;
    private String aprovadoPorNome;

    // aviso (nao bloqueio) de que esse periodo se sobrepoe ao de outro
    // colaborador da mesma equipe - quem decide o que fazer e o coordenador
    private boolean conflitoComEquipe;
    private List<String> colegasConflitantes;

    public PeriodoFeriasDTO() {
    }

    public PeriodoFeriasDTO(PeriodoFerias entity) {
        this(entity, Collections.emptyList());
    }

    public PeriodoFeriasDTO(PeriodoFerias entity, List<String> colegasConflitantes) {
        this.id = entity.getId();
        this.dataInicio = entity.getDataInicio();
        this.dataFim = entity.getDataFim();
        this.status = entity.getStatus();
        this.solicitanteNome = entity.getSolicitante() != null ? entity.getSolicitante().getNome() : null;
        this.equipeNome = (entity.getSolicitante() != null && entity.getSolicitante().getEquipe() != null)
                ? entity.getSolicitante().getEquipe().getNome() : null;
        this.aprovadoPorNome = entity.getAprovadoPor() != null ? entity.getAprovadoPor().getNome() : null;
        this.colegasConflitantes = colegasConflitantes;
        this.conflitoComEquipe = !colegasConflitantes.isEmpty();
    }

    public Long getId() {
        return id;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public StatusFerias getStatus() {
        return status;
    }

    public String getSolicitanteNome() {
        return solicitanteNome;
    }

    public String getEquipeNome() {
        return equipeNome;
    }

    public String getAprovadoPorNome() {
        return aprovadoPorNome;
    }

    public boolean isConflitoComEquipe() {
        return conflitoComEquipe;
    }

    public List<String> getColegasConflitantes() {
        return colegasConflitantes;
    }
}
