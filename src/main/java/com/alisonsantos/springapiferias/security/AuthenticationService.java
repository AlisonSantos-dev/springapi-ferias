package com.alisonsantos.springapiferias.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.alisonsantos.springapiferias.repositories.ColaboradorRepository;

@Service
public class AuthenticationService implements UserDetailsService {

    @Autowired
    private ColaboradorRepository colaboradorRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return colaboradorRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Colaborador nao encontrado: " + email));
    }
}
