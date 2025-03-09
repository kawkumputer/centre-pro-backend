package com.kawkumputer.centreeducatif.domain;

public enum ProjectStatus {
    DRAFT("Brouillon"),
    IN_PROGRESS("En cours"),
    ON_HOLD("En pause"),
    COMPLETED("Terminé"),
    CANCELLED("Annulé");

    private final String displayName;

    ProjectStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
