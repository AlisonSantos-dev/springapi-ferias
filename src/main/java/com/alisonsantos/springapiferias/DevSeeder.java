package com.alisonsantos.springapiferias;

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
@Profile("dev")
public class DevSeeder implements CommandLineRunner {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (colaboradorRepository.count() > 0) {
            // ja existe gente cadastrada, nao mexe em nada
            return;
        }

        Equipe equipe = new Equipe(null, "N1 - Suporte ANYMARKET");
        equipeRepository.save(equipe);

        Colaborador coordenador = new Colaborador(null, "Alison Santos",
                "alison.santos@db1.com.br", passwordEncoder.encode("senha1234"),
                Role.COORDENADOR, equipe);

        colaboradorRepository.save(coordenador);

        System.out.println(">>> DevSeeder: coordenador inicial criado. Troque a senha assim que logar.");
    }
}
