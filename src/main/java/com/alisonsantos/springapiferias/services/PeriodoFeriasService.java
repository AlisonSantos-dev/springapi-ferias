package com.alisonsantos.springapiferias.services;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.alisonsantos.springapiferias.dtos.PeriodoFeriasDTO;
import com.alisonsantos.springapiferias.dtos.PeriodoFeriasInsertDTO;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.entities.Equipe;
import com.alisonsantos.springapiferias.entities.PeriodoFerias;
import com.alisonsantos.springapiferias.entities.enums.StatusFerias;
import com.alisonsantos.springapiferias.repositories.PeriodoFeriasRepository;
import com.alisonsantos.springapiferias.services.exceptions.AcessoNegadoException;
import com.alisonsantos.springapiferias.services.exceptions.DatabaseException;
import com.alisonsantos.springapiferias.services.exceptions.OperacaoInvalidaException;
import com.alisonsantos.springapiferias.services.exceptions.PeriodoSobrepostoException;
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;

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

        // regra rigida: nao pode se sobrepor a um periodo do PROPRIO colaborador
        validarSobreposicaoPropria(solicitante.getId(), dto.getDataInicio(), dto.getDataFim());

        PeriodoFerias entity = new PeriodoFerias();
        entity.setDataInicio(dto.getDataInicio());
        entity.setDataFim(dto.getDataFim());
        entity.setStatus(StatusFerias.PENDENTE);
        entity.setSolicitante(solicitante);

        entity = periodoFeriasRepository.save(entity);

        // aviso (nao bloqueio): se sobrepoe ao periodo de outro colega da equipe
        return new PeriodoFeriasDTO(entity, colegasConflitantes(entity));
    }

    public List<PeriodoFeriasDTO> meusPeriodos() {
        Colaborador solicitante = colaboradorLogado();
        return periodoFeriasRepository.findBySolicitanteId(solicitante.getId())
                .stream()
                .map(p -> new PeriodoFeriasDTO(p, colegasConflitantes(p)))
                .collect(Collectors.toList());
    }

    public List<PeriodoFeriasDTO> porEquipe(Long equipeId) {
        return periodoFeriasRepository.findBySolicitante_Equipe_Id(equipeId)
                .stream()
                .map(p -> new PeriodoFeriasDTO(p, colegasConflitantes(p)))
                .collect(Collectors.toList());
    }

    // visao geral: todos os periodos, de todas as equipes - usado no
    // calendario e na listagem que qualquer colaborador ve
    public List<PeriodoFeriasDTO> todosOsPeriodos() {
        return periodoFeriasRepository.findAll()
                .stream()
                .map(p -> new PeriodoFeriasDTO(p, colegasConflitantes(p)))
                .collect(Collectors.toList());
    }

    public PeriodoFeriasDTO aprovar(Long id) {
        PeriodoFerias entity = buscarOuFalhar(id);
        entity.setStatus(StatusFerias.APROVADO);
        entity.setAprovadoPor(colaboradorLogado());
        entity = periodoFeriasRepository.save(entity);
        return new PeriodoFeriasDTO(entity, colegasConflitantes(entity));
    }

    public PeriodoFeriasDTO rejeitar(Long id) {
        PeriodoFerias entity = buscarOuFalhar(id);
        entity.setStatus(StatusFerias.REJEITADO);
        entity.setAprovadoPor(colaboradorLogado());
        entity = periodoFeriasRepository.save(entity);
        return new PeriodoFeriasDTO(entity, colegasConflitantes(entity));
    }

    public void cancelar(Long id) {
        PeriodoFerias entity = buscarOuFalhar(id);
        Colaborador logado = colaboradorLogado();

        if (!entity.getSolicitante().getId().equals(logado.getId())) {
            throw new AcessoNegadoException("Voce so pode cancelar seus proprios pedidos de ferias");
        }
        if (entity.getStatus() != StatusFerias.PENDENTE) {
            throw new OperacaoInvalidaException("Somente pedidos PENDENTE podem ser cancelados");
        }

        try {
            periodoFeriasRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException("Falha de integridade referencial ao cancelar o periodo");
        }
    }

    // ---- regra rigida: sobreposicao com o proprio colaborador ----

    private void validarSobreposicaoPropria(Long solicitanteId, LocalDate novaDataInicio, LocalDate novaDataFim) {
        List<PeriodoFerias> existentes = periodoFeriasRepository
                .findBySolicitanteIdAndStatusNot(solicitanteId, StatusFerias.REJEITADO);

        boolean sobrepoe = existentes.stream().anyMatch(p ->
                periodosSeSobrepoe(novaDataInicio, novaDataFim, p.getDataInicio(), p.getDataFim()));

        if (sobrepoe) {
            throw new PeriodoSobrepostoException(
                    "Ja existe um periodo de ferias cadastrado que se sobrepoe a essas datas");
        }
    }

    // ---- aviso (nao bloqueio): sobreposicao com colegas da mesma equipe ----

    private List<String> colegasConflitantes(PeriodoFerias periodo) {
        Colaborador solicitante = periodo.getSolicitante();
        Equipe equipe = solicitante != null ? solicitante.getEquipe() : null;
        if (equipe == null) {
            return List.of();
        }

        List<PeriodoFerias> daEquipe = periodoFeriasRepository
                .findBySolicitante_Equipe_IdAndStatusNot(equipe.getId(), StatusFerias.REJEITADO);

        return daEquipe.stream()
                .filter(p -> !p.getId().equals(periodo.getId()))
                .filter(p -> !p.getSolicitante().getId().equals(solicitante.getId()))
                .filter(p -> periodosSeSobrepoe(periodo.getDataInicio(), periodo.getDataFim(),
                        p.getDataInicio(), p.getDataFim()))
                .map(p -> p.getSolicitante().getNome())
                .distinct()
                .collect(Collectors.toList());
    }

    // dois intervalos se sobrepoem quando inicio1 <= fim2 E inicio2 <= fim1
    private boolean periodosSeSobrepoe(LocalDate inicio1, LocalDate fim1, LocalDate inicio2, LocalDate fim2) {
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
