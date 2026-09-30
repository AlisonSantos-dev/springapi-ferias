package com.alisonsantos.springapiferias.services;

import java.util.List;
import java.util.stream.Collectors;

import com.alisonsantos.springapiferias.dtos.ColaboradorCadastroDTO;
import com.alisonsantos.springapiferias.dtos.TrocarSenhaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.alisonsantos.springapiferias.dtos.ColaboradorDTO;
import com.alisonsantos.springapiferias.dtos.ColaboradorInsertDTO;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.entities.Equipe;
import com.alisonsantos.springapiferias.entities.enums.Role;
import com.alisonsantos.springapiferias.repositories.ColaboradorRepository;
import com.alisonsantos.springapiferias.repositories.EquipeRepository;
import com.alisonsantos.springapiferias.services.exceptions.AcessoNegadoException;
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;

@Service
public class ColaboradorService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // true no perfil dev/test (login por senha); false em producao, onde
    // todo mundo entra pela conta DB1 e ninguem tem senha no sistema
    @Value("${app.auth.senha-habilitada:false}")
    private boolean senhaHabilitada;

    public List<ColaboradorDTO> findAll() {
        return colaboradorRepository.findAll().stream().map(ColaboradorDTO::new).collect(Collectors.toList());
    }

    // usado pelo coordenador: pode escolher a role (inclusive criar outro
    // coordenador).
    // - Com login por senha (dev): a senha definida aqui e temporaria, a
    //   pessoa e obrigada a trocar no primeiro login.
    // - Com login pela conta DB1 (prod): nao tem senha. O coordenador so
    //   pre-cadastra o email, a equipe e o papel; quando a pessoa entrar
    //   pela conta DB1 ela e reconhecida pelo email.
    public ColaboradorDTO insert(ColaboradorInsertDTO dto) {
        validarCamposComuns(dto.getNome(), dto.getEmail(), dto.getSenha(), dto.getEquipeId(), senhaHabilitada);

        Equipe equipe = buscarEquipeOuFalhar(dto.getEquipeId());
        Role role = converterRole(dto.getRole());

        String senhaCriptografada = senhaHabilitada ? passwordEncoder.encode(dto.getSenha()) : null;
        Colaborador entity = new Colaborador(null, dto.getNome(), dto.getEmail().trim().toLowerCase(),
                senhaCriptografada, role, equipe);
        entity.setSenhaTemporaria(senhaHabilitada);

        entity = colaboradorRepository.save(entity);
        return new ColaboradorDTO(entity);
    }

    // cadastro publico (sem login): sempre cria como COLABORADOR, role nao e
    // aceita como entrada. A pessoa ja escolheu a propria senha, entao NAO
    // e marcada como temporaria.
    // Em producao fica desligado: o primeiro acesso pela conta DB1
    // (SsoService.primeiroAcesso) substitui esse cadastro.
    public ColaboradorDTO cadastrarPublico(ColaboradorCadastroDTO dto) {
        exigirSenhaHabilitada();
        validarCamposComuns(dto.getNome(), dto.getEmail(), dto.getSenha(), dto.getEquipeId(), true);

        Equipe equipe = buscarEquipeOuFalhar(dto.getEquipeId());

        Colaborador entity = new Colaborador(null, dto.getNome(), dto.getEmail(),
                passwordEncoder.encode(dto.getSenha()), Role.COLABORADOR, equipe);

        entity = colaboradorRepository.save(entity);
        return new ColaboradorDTO(entity);
    }

    // troca a propria senha (de quem estiver logado). Usado tanto pro fluxo
    // obrigatorio (senha temporaria) quanto pra troca voluntaria no futuro.
    public void trocarSenha(TrocarSenhaDTO dto) {
        exigirSenhaHabilitada();
        Colaborador logado = colaboradorLogado();

        if (dto.getSenhaAtual() == null || !passwordEncoder.matches(dto.getSenhaAtual(), logado.getPassword())) {
            throw new IllegalArgumentException("senha atual incorreta");
        }
        if (dto.getNovaSenha() == null || dto.getNovaSenha().length() < 6) {
            throw new IllegalArgumentException("a nova senha deve ter pelo menos 6 caracteres");
        }

        logado.setSenha(passwordEncoder.encode(dto.getNovaSenha()));
        logado.setSenhaTemporaria(false);
        colaboradorRepository.save(logado);
    }

    private void exigirSenhaHabilitada() {
        if (!senhaHabilitada) {
            throw new AcessoNegadoException("login por senha desativado neste ambiente: o acesso e pela conta DB1");
        }
    }

    private Colaborador colaboradorLogado() {
        return (Colaborador) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    private Equipe buscarEquipeOuFalhar(Long equipeId) {
        return equipeRepository.findById(equipeId)
                .orElseThrow(() -> new ResourceNotFoundException(equipeId));
    }

    private Role converterRole(String roleTexto) {
        try {
            return Role.valueOf(roleTexto.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("role invalida: use COLABORADOR ou COORDENADOR");
        }
    }

    private void validarCamposComuns(String nome, String email, String senha, Long equipeId, boolean exigirSenha) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
        if (exigirSenha && (senha == null || senha.length() < 6)) {
            throw new IllegalArgumentException("senha e obrigatoria e deve ter pelo menos 6 caracteres");
        }
        if (equipeId == null) {
            throw new IllegalArgumentException("equipeId e obrigatorio");
        }
        colaboradorRepository.findByEmailIgnoreCase(email.trim()).ifPresent(c -> {
            throw new IllegalArgumentException("ja existe um colaborador com esse email");
        });
    }
}
