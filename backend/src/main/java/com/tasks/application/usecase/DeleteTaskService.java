package com.tasks.application.usecase;

import com.tasks.application.port.input.DeleteTaskUseCase;
import com.tasks.application.port.output.TaskRepositoryPort;
import com.tasks.domain.exception.TaskNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DeleteTaskService implements DeleteTaskUseCase {

    private final TaskRepositoryPort taskRepositoryPort;

    public DeleteTaskService(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public void delete(UUID id) {
        if (!taskRepositoryPort.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepositoryPort.deleteById(id);
    }
}
