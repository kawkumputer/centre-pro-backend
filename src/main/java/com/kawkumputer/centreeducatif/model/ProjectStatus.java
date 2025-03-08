package com.kawkumputer.centreeducatif.model;

/**
 * Énumération des différents statuts possibles pour un projet.
 */
public enum ProjectStatus {
    PLANIFIE("Planifié"),
    EN_COURS("En cours"),
    EN_PAUSE("En pause"),
    TERMINE("Terminé"),
    ANNULE("Annulé");

    private final String label;

    ProjectStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
