package com.tasks.application.port.output;

import com.tasks.domain.model.Task;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepositoryPort {

    Task save(Task task);

    Optional<Task> findById(UUID id);

    List<Task> findAll();

    void deleteById(UUID id);

    boolean existsById(UUID id);
}
