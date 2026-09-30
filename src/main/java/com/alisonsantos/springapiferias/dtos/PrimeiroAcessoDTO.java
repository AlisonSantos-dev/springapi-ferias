package com.alisonsantos.springapiferias.dtos;

// Corpo do POST /api/auth/sso/primeiro-acesso. Nome e email NAO vem aqui de
// proposito: eles saem dos headers do gateway, que a pessoa nao consegue
// falsificar. A unica coisa que ela escolhe e a propria equipe.
public class PrimeiroAcessoDTO {

    private Long equipeId;

    public PrimeiroAcessoDTO() {
    }

    public Long getEquipeId() {
        return equipeId;
    }

    public void setEquipeId(Long equipeId) {
        this.equipeId = equipeId;
    }
}
