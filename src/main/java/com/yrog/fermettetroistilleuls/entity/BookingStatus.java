package com.yrog.fermettetroistilleuls.entity;

/**
 * Statut d'une demande de réservation
 * (gîte ou activité).
 */
public enum BookingStatus {

    /** Demande reçue, en attente de validation par la ferme. */
    PENDING,

    /** Demande acceptée — email de confirmation envoyé. */
    ACCEPTED,

    /** Demande refusée — email de refus envoyé. */
    REJECTED
}
