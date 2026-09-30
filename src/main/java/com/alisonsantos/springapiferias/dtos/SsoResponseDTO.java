package com.alisonsantos.springapiferias.dtos;

// Resposta do GET /api/auth/sso e do POST /api/auth/sso/primeiro-acesso.
//
// status = "AUTENTICADO"        -> token preenchido, o front guarda e entra
// status = "CADASTRO_PENDENTE"  -> a pessoa entrou com a conta DB1 mas ainda
//                                  nao existe no sistema; o front mostra a
//                                  tela de primeiro acesso (escolher equipe)
public class SsoResponseDTO {

    private String status;
    private String token;
    private String nome;
    private String email;

    public SsoResponseDTO() {
    }

    private SsoResponseDTO(String status, String token, String nome, String email) {
        this.status = status;
        this.token = token;
        this.nome = nome;
        this.email = email;
    }

    public static SsoResponseDTO autenticado(String token) {
        return new SsoResponseDTO("AUTENTICADO", token, null, null);
    }

    public static SsoResponseDTO cadastroPendente(String nome, String email) {
        return new SsoResponseDTO("CADASTRO_PENDENTE", null, nome, email);
    }

    public String getStatus() {
        return status;
    }

    public String getToken() {
        return token;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }
}
