package com.alisonsantos.springapiferias.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alisonsantos.springapiferias.dtos.PrimeiroAcessoDTO;
import com.alisonsantos.springapiferias.dtos.SsoResponseDTO;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.entities.Equipe;
import com.alisonsantos.springapiferias.entities.enums.Role;
import com.alisonsantos.springapiferias.repositories.ColaboradorRepository;
import com.alisonsantos.springapiferias.repositories.EquipeRepository;
import com.alisonsantos.springapiferias.security.IdentidadeGateway;
import com.alisonsantos.springapiferias.security.TokenService;
import com.alisonsantos.springapiferias.services.exceptions.AcessoNegadoException;
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;

// Login pela conta Microsoft da DB1, usando a identidade que o gateway do
// portal ja validou (ver IdentidadeGateway). Depois de reconhecer a pessoa,
// devolve o MESMO JWT que o login por senha devolve, entao o resto do
// sistema (SecurityFilter, roles, @PreAuthorize) continua igual.
@Service
public class SsoService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private TokenService tokenService;

    // Desligado por padrao: so o perfil prod liga, porque so em producao
    // existe o gateway garantindo que os headers X-DB1-* sao verdadeiros.
    @Value("${app.auth.sso-habilitado:false}")
    private boolean ssoHabilitado;

    public boolean isSsoHabilitado() {
        return ssoHabilitado;
    }

    @Transactional
    public SsoResponseDTO entrar(IdentidadeGateway identidade) {
        verificarHabilitado();

        Optional<Colaborador> existente = buscar(identidade);
        if (existente.isEmpty()) {
            return SsoResponseDTO.cadastroPendente(identidade.nome(), identidade.email());
        }

        Colaborador colaborador = existente.get();
        boolean alterou = false;

        // primeira vez que essa pessoa entra pela conta DB1 (ex.: Mateus e
        // Eliezer, que vieram do BootstrapSeeder): grava o oid dela
        if (colaborador.getOid() == null) {
            colaborador.setOid(identidade.oid());
            alterou = true;
        }
        // quem entra pela conta DB1 nao usa senha, entao a marca de senha
        // temporaria nao faz mais sentido (senao ficaria preso na tela de
        // troca de senha)
        if (colaborador.isSenhaTemporaria()) {
            colaborador.setSenhaTemporaria(false);
            alterou = true;
        }
        if (alterou) {
            colaborador = colaboradorRepository.save(colaborador);
        }

        return SsoResponseDTO.autenticado(tokenService.gerarToken(colaborador));
    }

    @Transactional
    public SsoResponseDTO primeiroAcesso(IdentidadeGateway identidade, PrimeiroAcessoDTO dto) {
        verificarHabilitado();

        // se a pessoa ja existe (ex.: clicou duas vezes), so faz o login
        if (buscar(identidade).isPresent()) {
            return entrar(identidade);
        }
        if (dto == null || dto.getEquipeId() == null) {
            throw new IllegalArgumentException("equipeId e obrigatorio");
        }

        Equipe equipe = equipeRepository.findById(dto.getEquipeId())
                .orElseThrow(() -> new ResourceNotFoundException(dto.getEquipeId()));

        // sempre COLABORADOR e sem senha: quem promove a coordenador e um
        // coordenador, pelo painel
        Colaborador novo = new Colaborador(null, identidade.nome(), identidade.email(),
                null, Role.COLABORADOR, equipe);
        novo.setOid(identidade.oid());
        novo = colaboradorRepository.save(novo);

        return SsoResponseDTO.autenticado(tokenService.gerarToken(novo));
    }

    // procura primeiro pelo oid (que nunca muda) e, se nao achar, pelo email
    // (caso de quem foi cadastrado antes de entrar pela conta DB1)
    private Optional<Colaborador> buscar(IdentidadeGateway identidade) {
        return colaboradorRepository.findByOid(identidade.oid())
                .or(() -> colaboradorRepository.findByEmailIgnoreCase(identidade.email()));
    }

    private void verificarHabilitado() {
        if (!ssoHabilitado) {
            throw new AcessoNegadoException("login pela conta DB1 desativado neste ambiente");
        }
    }
}
