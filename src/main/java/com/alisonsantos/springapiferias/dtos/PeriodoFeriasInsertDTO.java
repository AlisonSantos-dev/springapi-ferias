package com.alisonsantos.springapiferias.dtos;

import java.time.LocalDate;

public class PeriodoFeriasInsertDTO {

    private LocalDate dataInicio;
    private LocalDate dataFim;

    public PeriodoFeriasInsertDTO() {
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
}
