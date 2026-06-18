package com.yrog.fermettetroistilleuls.exception;

/**
 * Exception levée quand une ressource
 * n'est pas trouvée en base de données.
 * Ex: gîte introuvable, activité inexistante...
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
