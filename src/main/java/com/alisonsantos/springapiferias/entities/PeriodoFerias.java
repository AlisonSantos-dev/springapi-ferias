package com.alisonsantos.springapiferias.entities;

import com.alisonsantos.springapiferias.entities.enums.StatusFerias;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "tb_periodo_ferias")
public class PeriodoFerias implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate dataInicio;
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    private StatusFerias status;

    @ManyToOne
    @JoinColumn(name = "solicitante_id")
    private Colaborador solicitante;

    // pode ficar nulo enquanto o status estiver PENDENTE
    @ManyToOne
    @JoinColumn(name = "aprovado_por_id")
    private Colaborador aprovadoPor;

    public PeriodoFerias() {
    }

    public PeriodoFerias(Long id, LocalDate dataInicio, LocalDate dataFim, StatusFerias status,
                         Colaborador solicitante, Colaborador aprovadoPor) {
        this.id = id;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = status;
        this.solicitante = solicitante;
        this.aprovadoPor = aprovadoPor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public StatusFerias getStatus() {
        return status;
    }

    public void setStatus(StatusFerias status) {
        this.status = status;
    }

    public Colaborador getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Colaborador solicitante) {
        this.solicitante = solicitante;
    }

    public Colaborador getAprovadoPor() {
        return aprovadoPor;
    }

    public void setAprovadoPor(Colaborador aprovadoPor) {
        this.aprovadoPor = aprovadoPor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PeriodoFerias)) return false;
        PeriodoFerias that = (PeriodoFerias) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
