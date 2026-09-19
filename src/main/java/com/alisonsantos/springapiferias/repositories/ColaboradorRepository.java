package com.alisonsantos.springapiferias.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alisonsantos.springapiferias.entities.Colaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    Optional<Colaborador> findByEmail(String email);
}
