package com.tasks.domain.event;

import com.tasks.domain.model.TaskStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record TaskCreatedEvent(UUID taskId, String title, TaskStatus status, LocalDateTime occurredAt) {}
