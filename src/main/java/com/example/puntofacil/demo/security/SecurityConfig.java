package com.example.puntofacil.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;



@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public SecurityConfig(CustomUserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    // Authentication provider que usa nuestro UserDetailsService + encoder (NoOp en dev)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(userDetailsService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }

    // Manager (se usa internamente)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // Security filter chain
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
          .csrf(csrf -> csrf.disable()) // simplifies dev; production: enable + csrf token
          .authorizeHttpRequests(auth -> auth
              .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
              .requestMatchers("/", "/login", "/forgot", "/reset").permitAll()
              .requestMatchers("/admin/**").hasRole("DUEÑO")
              .requestMatchers("/cajera/**").hasRole("CAJERA")
              .requestMatchers("/vendedor/**").hasRole("VENDEDOR")
              .anyRequest().authenticated()
          )
          .formLogin(form -> form
              .loginPage("/login")
              .loginProcessingUrl("/login")
              .usernameParameter("username")
              .passwordParameter("password")
              .defaultSuccessUrl("/home", true)
              .permitAll()
          )
          .logout(logout -> logout
              .logoutUrl("/logout")
              .logoutSuccessUrl("/login?logout")
              .permitAll()
          );

        return http.build();
    }
}
