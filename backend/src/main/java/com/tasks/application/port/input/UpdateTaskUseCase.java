package com.tasks.application.port.input;

import com.tasks.domain.model.Task;
import com.tasks.domain.model.TaskStatus;

import java.util.UUID;

public interface UpdateTaskUseCase {

    Task update(UUID id, UpdateTaskCommand command);

    record UpdateTaskCommand(String title, String description, TaskStatus status) {}
}
