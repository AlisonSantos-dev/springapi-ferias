package com.alisonsantos.springapiferias.resources;

import org.springframework.beans.factory.annotation.Autowired;
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

@RestController
@RequestMapping("/api/login")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO dto) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getSenha());
        Authentication authentication = authenticationManager.authenticate(authToken);

        Colaborador colaborador = (Colaborador) authentication.getPrincipal();
        String token = tokenService.gerarToken(colaborador);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }
}
