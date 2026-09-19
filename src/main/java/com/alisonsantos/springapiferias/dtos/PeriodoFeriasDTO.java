package com.alisonsantos.springapiferias.dtos;

import com.alisonsantos.springapiferias.entities.PeriodoFerias;
import com.alisonsantos.springapiferias.entities.enums.StatusFerias;

import java.time.LocalDate;

public class PeriodoFeriasDTO {


    private Long id;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private StatusFerias status;
    private String solicitanteNome;
    private String aprovadoPorNome;

    public PeriodoFeriasDTO() {
    }

    public PeriodoFeriasDTO(PeriodoFerias entity) {
        this.id = entity.getId();
        this.dataInicio = entity.getDataInicio();
        this.dataFim = entity.getDataFim();
        this.status = entity.getStatus();
        this.solicitanteNome = entity.getSolicitante() != null ? entity.getSolicitante().getNome() : null;
        this.aprovadoPorNome = entity.getAprovadoPor() != null ? entity.getAprovadoPor().getNome() : null;
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

    public String getAprovadoPorNome() {
        return aprovadoPorNome;
    }
}
