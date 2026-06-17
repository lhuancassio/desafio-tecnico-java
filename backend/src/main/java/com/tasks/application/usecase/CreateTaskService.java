package com.tasks.application.usecase;

import com.tasks.application.port.input.CreateTaskUseCase;
import com.tasks.application.port.output.TaskEventPort;
import com.tasks.application.port.output.TaskRepositoryPort;
import com.tasks.domain.event.TaskCreatedEvent;
import com.tasks.domain.model.Task;
import com.tasks.domain.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CreateTaskService implements CreateTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;
    private final TaskEventPort taskEventPort;

    public CreateTaskService(TaskRepositoryPort taskRepositoryPort, TaskEventPort taskEventPort) {
        this.taskRepositoryPort = taskRepositoryPort;
        this.taskEventPort = taskEventPort;
    }

    @Override
    public Task create(CreateTaskCommand command) {
        var task = new Task(null, command.title(), command.description(), TaskStatus.PENDING, LocalDateTime.now());
        var saved = taskRepositoryPort.save(task);
        taskEventPort.publishTaskCreated(
            new TaskCreatedEvent(saved.getId(), saved.getTitle(), saved.getStatus(), LocalDateTime.now())
        );
        return saved;
    }
}
