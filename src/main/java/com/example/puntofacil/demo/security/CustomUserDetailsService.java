package com.example.puntofacil.demo.security;

import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.puntofacil.demo.entity.Empleado;
import com.example.puntofacil.demo.repository.EmpleadoRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final EmpleadoRepository empleadoRepository;

    public CustomUserDetailsService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Empleado empleado = empleadoRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        // ⚠ Ojo: como en la entidad Rol lo tenés como String, no uses .name()
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + empleado.getRol());

        return new User(
                empleado.getUsername(),
                empleado.getPassword(), // ✅ usamos password en texto plano
                Collections.singletonList(authority)
        );
    }
}