package com.yrog.fermettetroistilleuls.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Intercepteur limitant le nombre de requêtes par IP
 * sur les formulaires publics sensibles (contact, réservation).
 * Protège contre le spam et les attaques par force brute.
 */
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(RateLimitInterceptor.class);

    /**
     * Cache des buckets par adresse IP.
     * Un bucket = un compteur de requêtes par IP.
     */
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    /**
     * Crée ou récupère le bucket de rate limiting pour une IP donnée.
     * Limite : 10 requêtes par minute maximum.
     *
     * @param ip adresse IP du client
     * @return le bucket correspondant
     */
    private Bucket resolveBucket(String ip) {
        return buckets.computeIfAbsent(ip, key -> {
            Bandwidth limit = Bandwidth.classic(
                    10,
                    Refill.greedy(10, Duration.ofMinutes(1))
            );
            return Bucket.builder().addLimit(limit).build();
        });
    }

    /**
     * Vérifie si la requête dépasse la limite de taux.
     * Si oui, retourne une erreur 429 (Too Many Requests).
     *
     * @param request  requête HTTP entrante
     * @param response réponse HTTP
     * @param handler  le handler cible
     * @return true si la requête est autorisée, false sinon
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String ip = request.getRemoteAddr();
        Bucket bucket = resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            return true;
        }

        log.warn("Rate limit dépassé pour l'IP : {}", ip);
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.getWriter().write("Trop de requêtes. Veuillez patienter avant de réessayer.");
        return false;
    }
}