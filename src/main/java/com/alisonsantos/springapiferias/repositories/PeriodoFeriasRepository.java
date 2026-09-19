package com.alisonsantos.springapiferias.repositories;

import com.alisonsantos.springapiferias.entities.PeriodoFerias;
import com.alisonsantos.springapiferias.entities.enums.StatusFerias;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PeriodoFeriasRepository extends JpaRepository<PeriodoFerias, Long> {

    List<PeriodoFerias> findBySolicitanteId(Long solicitanteId);

    List<PeriodoFerias> findBySolicitante_Equipe_Id(Long equipeId);

    // usado na validacao de sobreposicao: pega os periodos do colaborador
    // que ainda "contam" (PENDENTE ou APROVADO), ignorando os REJEITADO
    List<PeriodoFerias> findBySolicitanteIdAndStatusNot(Long solicitanteId, StatusFerias status);
}
