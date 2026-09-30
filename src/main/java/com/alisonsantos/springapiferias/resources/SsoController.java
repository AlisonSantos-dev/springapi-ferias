package com.alisonsantos.springapiferias.resources;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alisonsantos.springapiferias.dtos.PrimeiroAcessoDTO;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.security.IdentidadeGateway;
import com.alisonsantos.springapiferias.services.SsoService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class SsoController {

    @Autowired
    private SsoService ssoService;

    @Value("${app.auth.senha-habilitada:false}")
    private boolean senhaHabilitada;

    // O front chama isso ao abrir a tela de login pra saber quais formas de
    // entrar existem neste ambiente (prod: so conta DB1; dev: so senha).
    @GetMapping("/config")
    public ResponseEntity<Map<String, Boolean>> config() {
        return ResponseEntity.ok(Map.of(
                "ssoHabilitado", ssoService.isSsoHabilitado(),
                "senhaHabilitada", senhaHabilitada));
    }

    // O front chama isso ao abrir o app, se tiver um token guardado, pra saber
    // se ele ainda vale (pode ter expirado, ou ter sido assinado com outra
    // chave). 204 = vale; 401 = o front limpa tudo e volta pro login.
    @GetMapping("/sessao")
    public ResponseEntity<Void> sessao() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Colaborador) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Entra com a conta DB1. Tambem serve de diagnostico: abrindo
    // /api/auth/sso direto no navegador da pra ver se os headers do gateway
    // estao chegando (401 = nao chegaram).
    @GetMapping("/sso")
    public ResponseEntity<?> entrar(HttpServletRequest request) {
        Optional<IdentidadeGateway> identidade = IdentidadeGateway.daRequisicao(request);
        if (identidade.isEmpty()) {
            return semIdentidade();
        }
        return ResponseEntity.ok(ssoService.entrar(identidade.get()));
    }

    // Primeiro acesso: a pessoa entrou com a conta DB1, ainda nao existe no
    // sistema e escolheu a equipe dela.
    @PostMapping("/sso/primeiro-acesso")
    public ResponseEntity<?> primeiroAcesso(HttpServletRequest request, @RequestBody PrimeiroAcessoDTO dto) {
        Optional<IdentidadeGateway> identidade = IdentidadeGateway.daRequisicao(request);
        if (identidade.isEmpty()) {
            return semIdentidade();
        }
        return ResponseEntity.ok(ssoService.primeiroAcesso(identidade.get(), dto));
    }

    private ResponseEntity<Map<String, String>> semIdentidade() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "message", "identidade da conta DB1 nao recebida (headers X-DB1-* ausentes)"));
    }
}
