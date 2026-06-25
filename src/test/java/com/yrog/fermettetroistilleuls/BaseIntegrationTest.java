package com.yrog.fermettetroistilleuls;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Classe de base pour tous les tests d'intégration.
 * Démarre un vrai conteneur PostgreSQL via Testcontainers.
 * Le conteneur est partagé entre tous les tests (réutilisé).
 */
@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("fermette_test")
                    .withUsername("test")
                    .withPassword("test");

    /**
     * Injecte dynamiquement les propriétés de connexion
     * du conteneur PostgreSQL dans le contexte Spring.
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("ADMIN_PASSWORD", () -> "TestPassword123");
    }
}
