package com.alisonsantos.springapiferias.security;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

// Quem a pessoa e, segundo o gateway de identidade do Portal de Deploy DB1.
//
// Em producao, antes de qualquer requisicao chegar no Spring, o gateway do
// portal ja fez o login da pessoa com a conta Microsoft da DB1 e injeta tres
// headers ja validados:
//
//   X-DB1-Oid    identificador fixo da pessoa (nunca muda)
//   X-DB1-Email  email corporativo
//   X-DB1-Name   nome completo
//
// Da pra confiar nesses headers porque o proxy do portal APAGA qualquer
// X-DB1-* que venha de fora antes de colocar os dele. Fora do portal (no seu
// IntelliJ, por exemplo) ninguem apaga nada, e por isso o login pela conta
// DB1 so e ligado no perfil prod (app.auth.sso-habilitado).
public record IdentidadeGateway(String oid, String email, String nome) {

    public static Optional<IdentidadeGateway> daRequisicao(HttpServletRequest request) {
        String oid = limpar(request.getHeader("X-DB1-Oid"));
        String email = limpar(request.getHeader("X-DB1-Email"));
        String nome = limpar(request.getHeader("X-DB1-Name"));

        // sem oid ou sem email nao da pra saber quem e: recusa
        if (oid == null || email == null) {
            return Optional.empty();
        }
        if (nome == null) {
            // sem nome, usa a parte antes do @ (ex.: "mateus.seron")
            nome = email.substring(0, email.indexOf('@') > 0 ? email.indexOf('@') : email.length());
        }
        return Optional.of(new IdentidadeGateway(oid, email.toLowerCase(), corrigirAcentos(nome)));
    }

    private static String limpar(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        return valor.trim();
    }

    // O Tomcat le headers como ISO-8859-1. Se o gateway mandar o nome em
    // UTF-8 (o mais comum), "Joao" com til chegaria embaralhado ("JoÃ£o").
    // Aqui tentamos reinterpretar como UTF-8; se o resultado tiver caractere
    // invalido, e porque o valor original ja estava certo, e mantemos ele.
    private static String corrigirAcentos(String valor) {
        String reinterpretado = new String(valor.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        return reinterpretado.contains("�") ? valor : reinterpretado;
    }
}
