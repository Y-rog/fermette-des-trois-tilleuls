package com.yrog.fermettetroistilleuls.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité Spring Security.
 * Protège toutes les routes /admin/** avec une authentification.
 * Le mot de passe admin est lu depuis la variable d'environnement
 * ADMIN_PASSWORD — l'application refuse de démarrer si elle
 * n'est pas définie.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Configure la chaîne de filtres de sécurité :
     * - Routes /admin/** protégées par le rôle ADMIN
     * - Page de login personnalisée
     * - Logout sur /admin/logout
     * - Session toujours créée (évite les problèmes CSRF)
     * - Timeout de session configuré dans application.yml
     *
     * @param http le builder de sécurité HTTP
     * @return la chaîne de filtres configurée
     * @throws Exception si la configuration échoue
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                )
                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .defaultSuccessUrl("/admin/dashboard")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Bean de chiffrement des mots de passe.
     * Utilise BCrypt (algorithme sécurisé avec sel aléatoire).
     *
     * @return l'encodeur BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configure l'utilisateur admin en mémoire.
     * Le mot de passe est lu depuis la variable d'environnement
     * ADMIN_PASSWORD. Si elle n'est pas définie, l'application
     * refuse de démarrer pour éviter un mot de passe par défaut
     * dangereux en production.
     *
     * @return le gestionnaire d'utilisateurs en mémoire
     * @throws IllegalStateException si ADMIN_PASSWORD n'est pas définie
     */
    @Bean
    public UserDetailsService userDetailsService() {
        String adminPassword = System.getenv("ADMIN_PASSWORD");

        if (adminPassword == null || adminPassword.isBlank()) {
            throw new IllegalStateException(
                    "Variable d'environnement ADMIN_PASSWORD non définie ! " +
                            "Ajoutez-la dans votre .env ou vos variables Railway."
            );
        }

        UserDetails admin = User.builder()
                .username("admin")
                .password(passwordEncoder().encode(adminPassword))
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(admin);
    }
}
