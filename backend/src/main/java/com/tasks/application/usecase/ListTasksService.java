package com.tasks.application.usecase;

import com.tasks.application.port.input.ListTasksUseCase;
import com.tasks.application.port.output.TaskRepositoryPort;
import com.tasks.domain.model.Task;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListTasksService implements ListTasksUseCase {

    private final TaskRepositoryPort taskRepositoryPort;

    public ListTasksService(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public List<Task> listAll() {
        return taskRepositoryPort.findAll();
    }
}
