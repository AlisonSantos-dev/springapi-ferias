package com.alisonsantos.springapiferias.resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alisonsantos.springapiferias.dtos.LoginRequestDTO;
import com.alisonsantos.springapiferias.dtos.LoginResponseDTO;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.security.TokenService;
import com.alisonsantos.springapiferias.services.exceptions.AcessoNegadoException;

@RestController
@RequestMapping("/api/login")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    // Login por email e senha. Desligado por padrao: so o perfil dev (e o
    // test) liga. Em producao todo mundo entra pela conta DB1 (SsoController),
    // e deixar a senha ligada la seria uma porta dos fundos, ja que a senha
    // temporaria inicial do BootstrapSeeder esta escrita no codigo.
    @Value("${app.auth.senha-habilitada:false}")
    private boolean senhaHabilitada;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        if (!senhaHabilitada) {
            throw new AcessoNegadoException("login por senha desativado neste ambiente: entre com a conta DB1");
        }

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha());
        Authentication authentication = authenticationManager.authenticate(authToken);

        Colaborador colaborador = (Colaborador) authentication.getPrincipal();
        String token = tokenService.gerarToken(colaborador);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
