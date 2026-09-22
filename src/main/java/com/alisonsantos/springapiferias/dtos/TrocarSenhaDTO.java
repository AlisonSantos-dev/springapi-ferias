package com.alisonsantos.springapiferias.dtos;

public class TrocarSenhaDTO {

    private String senhaAtual;
    private String novaSenha;

    public TrocarSenhaDTO() {
    }

    public String getSenhaAtual() {
        return senhaAtual;
    }

    public void setSenhaAtual(String senhaAtual) {
        this.senhaAtual = senhaAtual;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(String novaSenha) {
        this.novaSenha = novaSenha;
    }
}
