package com.tasks.api.dto;

import com.tasks.domain.model.Task;
import com.tasks.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskResponse(
    UUID id,
    String title,
    String description,
    TaskStatus status,
    String statusDisplayName,
    LocalDateTime createdAt
) {
    public static TaskResponse from(Task task) {
        return new TaskResponse(
            task.getId(),
            task.getTitle(),
            task.getDescription(),
            task.getStatus(),
            task.getStatus().getDisplayName(),
            task.getCreatedAt()
        );
    }
}
