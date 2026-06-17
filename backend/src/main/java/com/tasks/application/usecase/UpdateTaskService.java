package com.tasks.application.usecase;

import com.tasks.application.port.input.UpdateTaskUseCase;
import com.tasks.application.port.output.TaskEventPort;
import com.tasks.application.port.output.TaskRepositoryPort;
import com.tasks.domain.event.TaskCreatedEvent;
import com.tasks.domain.exception.TaskNotFoundException;
import com.tasks.domain.model.Task;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class UpdateTaskService implements UpdateTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final TaskEventPort taskEventPort;

    public UpdateTaskService(TaskRepositoryPort taskRepositoryPort, TaskEventPort taskEventPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.taskEventPort = taskEventPort;
    }

    @Override
    public Task update(UUID id, UpdateTaskCommand command) {
        var task = taskRepositoryPort.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));

        task.setTitle(command.title());
        task.setDescription(command.description());
        task.setStatus(command.status());

        var saved = taskRepositoryPort.save(task);

        taskEventPort.publishTaskUpdated(
            new TaskCreatedEvent(saved.getId(), saved.getTitle(), saved.getStatus(), LocalDateTime.now())
        );

        return saved;
    }
}
