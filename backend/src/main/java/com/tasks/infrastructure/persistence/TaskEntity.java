package com.tasks.infrastructure.persistence;

import com.tasks.domain.model.Task;
import com.tasks.domain.model.TaskStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
public class TaskEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public static TaskEntity from(Task task) {
        var entity = new TaskEntity();
        entity.id = task.getId();
        entity.title = task.getTitle();
        entity.description = task.getDescription();
        entity.status = task.getStatus();
        entity.createdAt = task.getCreatedAt();
        return entity;
    }

    public Task toDomain() {
        return new Task(id, title, description, status, createdAt);
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public TaskStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
