package com.alisonsantos.springapiferias.repositories;

import com.alisonsantos.springapiferias.entities.Equipe;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipeRepository extends JpaRepository<Equipe, Long> {
}
