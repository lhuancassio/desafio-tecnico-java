package com.tasks.domain.model;

public enum TaskStatus {
    PENDING("Pendente"),
    IN_PROGRESS("Em andamento"),
    DONE("Concluído");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
