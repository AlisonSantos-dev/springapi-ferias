package com.alisonsantos.springapiferias.services;

import com.alisonsantos.springapiferias.dtos.PeriodoFeriasDTO;
import com.alisonsantos.springapiferias.dtos.PeriodoFeriasInsertDTO;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.entities.PeriodoFerias;
import com.alisonsantos.springapiferias.entities.enums.StatusFerias;
import com.alisonsantos.springapiferias.repositories.PeriodoFeriasRepository;
import com.alisonsantos.springapiferias.services.exceptions.PeriodoSobrepostoException;
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
public class PeriodoFeriasService {

    @Autowired
    private PeriodoFeriasRepository periodoFeriasRepository;

    public PeriodoFeriasDTO solicitar(PeriodoFeriasInsertDTO dto) {
        Colaborador solicitante = colaboradorLogado();

        if (dto.getDataInicio() == null || dto.getDataFim() == null) {
            throw new IllegalArgumentException("Data de Inicio e Data de Fim sao obrigatorios");
        }
        if (dto.getDataInicio().isAfter(dto.getDataFim())) {
            throw new IllegalArgumentException("Data de Inicio nao pode ser depois de Data de Fim");
        }

        validarSobreposicao(solicitante.getId(), dto.getDataInicio(), dto.getDataFim());

        PeriodoFerias entity = new PeriodoFerias();
        entity.setDataInicio(dto.getDataInicio());
        entity.setDataFim(dto.getDataFim());
        entity.setStatus(StatusFerias.PENDENTE);
        entity.setSolicitante(solicitante);

        entity = periodoFeriasRepository.save(entity);
        return new PeriodoFeriasDTO(entity);
    }

    public List<PeriodoFeriasDTO> meusPeriodos() {
        Colaborador solicitante = colaboradorLogado();
        return periodoFeriasRepository.findBySolicitanteId(solicitante.getId())
                .stream().map(PeriodoFeriasDTO::new).collect(Collectors.toList());
    }

    public List<PeriodoFeriasDTO> porEquipe(Long equipeId) {
        return periodoFeriasRepository.findBySolicitante_Equipe_Id(equipeId)
                .stream().map(PeriodoFeriasDTO::new).collect(Collectors.toList());
    }

    public PeriodoFeriasDTO aprovar(Long id) {
        PeriodoFerias entity = buscarOuFalhar(id);
        entity.setStatus(StatusFerias.APROVADO);
        entity.setAprovadoPor(colaboradorLogado());
        entity = periodoFeriasRepository.save(entity);
        return new PeriodoFeriasDTO(entity);
    }

    public PeriodoFeriasDTO rejeitar(Long id) {
        PeriodoFerias entity = buscarOuFalhar(id);
        entity.setStatus(StatusFerias.REJEITADO);
        entity.setAprovadoPor(colaboradorLogado());
        entity = periodoFeriasRepository.save(entity);
        return new PeriodoFeriasDTO(entity);
    }

    // ---- regra de sobreposicao ----

    private void validarSobreposicao(Long solicitanteId, java.time.LocalDate novaDataInicio,
                                     java.time.LocalDate novaDataFim) {
        List<PeriodoFerias> existentes = periodoFeriasRepository
                .findBySolicitanteIdAndStatusNot(solicitanteId, StatusFerias.REJEITADO);

        boolean sobrepoe = existentes.stream().anyMatch(p ->
                periodosSeSobrepoe(novaDataInicio, novaDataFim, p.getDataInicio(), p.getDataFim()));

        if (sobrepoe) {
            throw new PeriodoSobrepostoException(
                    "Ja existe um periodo de ferias cadastrado que se sobrepoe a essas datas");
        }
    }

    // dois intervalos se sobrepoem quando inicio1 <= fim2 E inicio2 <= fim1
    private boolean periodosSeSobrepoe(java.time.LocalDate inicio1, java.time.LocalDate fim1,
                                       java.time.LocalDate inicio2, java.time.LocalDate fim2) {
        return !inicio1.isAfter(fim2) && !inicio2.isAfter(fim1);
    }

    private PeriodoFerias buscarOuFalhar(Long id) {
        return periodoFeriasRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(id));
    }

    private Colaborador colaboradorLogado() {
        return (Colaborador) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}