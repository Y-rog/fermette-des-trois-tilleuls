package com.yrog.fermedufay.entity;

/**
 * Statut de disponibilité d'un gîte
 * pour une date donnée.
 */
public enum AvailabilityStatus {

    /** Le gîte est disponible à cette date. */
    AVAILABLE,

    /** Le gîte est indisponible (bloqué par la ferme). */
    UNAVAILABLE,

    /** Le gîte est réservé par un client. */
    RESERVED
}
