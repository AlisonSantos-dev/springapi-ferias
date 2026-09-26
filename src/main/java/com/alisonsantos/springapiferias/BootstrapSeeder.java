package com.alisonsantos.springapiferias;

// Roda uma unica vez, so se o banco estiver vazio (nenhum colaborador
// cadastrado ainda). Cria as duas equipes reais e os dois coordenadores
// reais (Mateus e Eliezer), cada um com senha temporaria - eles sao
// obrigados a trocar por uma senha propria no primeiro login (mesma
// funcionalidade que ja usamos pro coordenador cadastrando colaborador
// pelo painel). Nenhum dado ficticio fica no banco.
//
// Depois que os dois logarem e trocarem a senha, todo o resto do time
// se autocadastra normalmente pela tela de cadastro publico (sempre como
// COLABORADOR), e os coordenadores usam o painel deles pra promover
// alguem a coordenador, se precisar de um terceiro no futuro.

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.entities.Equipe;
import com.alisonsantos.springapiferias.entities.enums.Role;
import com.alisonsantos.springapiferias.repositories.ColaboradorRepository;
import com.alisonsantos.springapiferias.repositories.EquipeRepository;

@Configuration
@Profile({"dev", "prod"})
public class BootstrapSeeder implements CommandLineRunner {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // TROQUE esse valor antes do primeiro deploy em producao, e avise o
    // Mateus e o Eliezer por um canal seguro (nao por e-mail em texto puro).
    private static final String SENHA_TEMPORARIA_INICIAL = "TrocarSenha@2026";

    @Override
    public void run(String... args) throws Exception {
        if (colaboradorRepository.count() > 0) {
            // ja existe gente cadastrada, nao mexe em nada
            return;
        }

        Equipe reativo = new Equipe(null, "Suporte ANYMARKET - Reativo");
        Equipe proAtivo = new Equipe(null, "Suporte ANYMARKET - ProAtivo");
        equipeRepository.save(reativo);
        equipeRepository.save(proAtivo);

        Colaborador mateus = new Colaborador(null, "Mateus Seron",
                "mateus.seron@db1.com.br", passwordEncoder.encode(SENHA_TEMPORARIA_INICIAL),
                Role.COORDENADOR, reativo);
        mateus.setSenhaTemporaria(true);

        Colaborador eliezer = new Colaborador(null, "Eliezer Martinez",
                "eliezer.martinez@db1.com.br", passwordEncoder.encode(SENHA_TEMPORARIA_INICIAL),
                Role.COORDENADOR, proAtivo);
        eliezer.setSenhaTemporaria(true);

        colaboradorRepository.save(mateus);
        colaboradorRepository.save(eliezer);

        System.out.println(">>> BootstrapSeeder: Mateus e Eliezer criados como coordenadores. "
                + "Avise os dois da senha temporaria por um canal seguro.");
    }
}
