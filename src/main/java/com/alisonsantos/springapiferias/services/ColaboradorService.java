package com.alisonsantos.springapiferias.services;

import java.util.List;
import java.util.stream.Collectors;

import com.alisonsantos.springapiferias.dtos.ColaboradorCadastroDTO;
import com.alisonsantos.springapiferias.dtos.TrocarSenhaDTO;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.alisonsantos.springapiferias.services.exceptions.ResourceNotFoundException;

@Service
public class ColaboradorService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<ColaboradorDTO> findAll() {
        return colaboradorRepository.findAll().stream().map(ColaboradorDTO::new).collect(Collectors.toList());
    }

    // usado pelo coordenador: pode escolher a role (inclusive criar outro
    // coordenador). A senha definida aqui e temporaria - a pessoa e obrigada
    // a trocar no primeiro login.
    public ColaboradorDTO insert(ColaboradorInsertDTO dto) {
        validarCamposComuns(dto.getNome(), dto.getEmail(), dto.getSenha(), dto.getEquipeId());

        Equipe equipe = buscarEquipeOuFalhar(dto.getEquipeId());
        Role role = converterRole(dto.getRole());

        Colaborador entity = new Colaborador(null, dto.getNome(), dto.getEmail(),
                passwordEncoder.encode(dto.getSenha()), role, equipe);
        entity.setSenhaTemporaria(true);

        entity = colaboradorRepository.save(entity);
        return new ColaboradorDTO(entity);
    }

    // cadastro publico (sem login): sempre cria como COLABORADOR, role nao e
    // aceita como entrada. A pessoa ja escolheu a propria senha, entao NAO
    // e marcada como temporaria.
    public ColaboradorDTO cadastrarPublico(ColaboradorCadastroDTO dto) {
        validarCamposComuns(dto.getNome(), dto.getEmail(), dto.getSenha(), dto.getEquipeId());

        Equipe equipe = buscarEquipeOuFalhar(dto.getEquipeId());

        Colaborador entity = new Colaborador(null, dto.getNome(), dto.getEmail(),
                passwordEncoder.encode(dto.getSenha()), Role.COLABORADOR, equipe);

        entity = colaboradorRepository.save(entity);
        return new ColaboradorDTO(entity);
    }

    // troca a propria senha (de quem estiver logado). Usado tanto pro fluxo
    // obrigatorio (senha temporaria) quanto pra troca voluntaria no futuro.
    public void trocarSenha(TrocarSenhaDTO dto) {
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

    private void validarCamposComuns(String nome, String email, String senha, Long equipeId) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("nome e obrigatorio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("email e obrigatorio");
        }
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("senha e obrigatoria e deve ter pelo menos 6 caracteres");
        }
        if (equipeId == null) {
            throw new IllegalArgumentException("equipeId e obrigatorio");
        }
        colaboradorRepository.findByEmail(email).ifPresent(c -> {
            throw new IllegalArgumentException("ja existe um colaborador com esse email");
        });
    }
}
