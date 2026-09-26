package com.alisonsantos.springapiferias.resources;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

// Rota leve, sem autenticacao e sem tocar no banco - o Coolify (por tras do
// portal da DB1) chama essa rota de dentro do proprio container pra saber se
// a versao nova do app ja esta pronta pra receber trafego, antes de derrubar
// a versao antiga.
@RestController
public class HealthResource {

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("ok");
    }
}
