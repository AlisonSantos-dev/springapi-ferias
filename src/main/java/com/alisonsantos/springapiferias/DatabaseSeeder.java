package com.alisonsantos.springapiferias;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alisonsantos.springapiferias.entities.enums.Role;
import com.alisonsantos.springapiferias.entities.enums.StatusFerias;
import com.alisonsantos.springapiferias.entities.Colaborador;
import com.alisonsantos.springapiferias.entities.Equipe;
import com.alisonsantos.springapiferias.entities.PeriodoFerias;
import com.alisonsantos.springapiferias.repositories.ColaboradorRepository;
import com.alisonsantos.springapiferias.repositories.EquipeRepository;
import com.alisonsantos.springapiferias.repositories.PeriodoFeriasRepository;


@Configuration
@Profile("test")
public class DatabaseSeeder implements CommandLineRunner {

    @Autowired
    private EquipeRepository equipeRepository;

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Autowired
    private PeriodoFeriasRepository periodoFeriasRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        // Equipe
        Equipe eq1 = new Equipe(null, "N1 - Suporte ANYMARKET");
        equipeRepository.save(eq1);

        // Colaboradores
        Colaborador c1 = new Colaborador(null, "Alison Santos", "alison.santos@db1.com.br",
                passwordEncoder.encode("123456"), Role.COLABORADOR, eq1);

        Colaborador c2 = new Colaborador(null, "Roger Lima", "roger.lima@db1.com.br",
                passwordEncoder.encode("123456"), Role.COORDENADOR, eq1);

        colaboradorRepository.save(c1);
        colaboradorRepository.save(c2);

        // Periodos de ferias de exemplo
        PeriodoFerias pf1 = new PeriodoFerias(null,
                LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 16),
                StatusFerias.APROVADO, c1, c2);

        PeriodoFerias pf2 = new PeriodoFerias(null,
                LocalDate.of(2027, 1, 10), LocalDate.of(2027, 1, 24),
                StatusFerias.PENDENTE, c1, null);

        PeriodoFerias pf3 = new PeriodoFerias(null,
                LocalDate.of(2027, 2, 1), LocalDate.of(2027, 2, 10),
                StatusFerias.PENDENTE, c2, null);

        periodoFeriasRepository.save(pf1);
        periodoFeriasRepository.save(pf2);
        periodoFeriasRepository.save(pf3);
    }
}