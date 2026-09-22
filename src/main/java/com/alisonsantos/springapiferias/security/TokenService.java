package com.alisonsantos.springapiferias.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alisonsantos.springapiferias.entities.Colaborador;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String gerarToken(Colaborador colaborador) {
        Date agora = new Date();
        Date expira = new Date(agora.getTime() + expiration);

        var builder = Jwts.builder()
                .subject(colaborador.getEmail())
                .claim("role", colaborador.getRole().name())
                .claim("nome", colaborador.getNome())
                .claim("senhaTemporaria", colaborador.isSenhaTemporaria())
                .issuedAt(agora)
                .expiration(expira)
                .signWith(getSigningKey());
        if (colaborador.getEquipe() != null) {
            builder.claim("equipeId", colaborador.getEquipe().getId());
        }

        return builder.compact();
    }

    public String extrairEmail(String token) {
        return extrairClaims(token).getSubject();
    }

    public boolean tokenValido(String token) {
        try {
            Date expiracao = extrairClaims(token).getExpiration();
            return expiracao.after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extrairClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
