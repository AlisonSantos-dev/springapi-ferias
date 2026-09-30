package com.alisonsantos.springapiferias.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alisonsantos.springapiferias.entities.Colaborador;

public interface ColaboradorRepository extends JpaRepository<Colaborador, Long> {

    Optional<Colaborador> findByEmail(String email);

    // usado no login pela conta DB1: o email que vem do gateway pode chegar
    // com maiusculas diferentes do que esta gravado no banco
    Optional<Colaborador> findByEmailIgnoreCase(String email);

    Optional<Colaborador> findByOid(String oid);
}
