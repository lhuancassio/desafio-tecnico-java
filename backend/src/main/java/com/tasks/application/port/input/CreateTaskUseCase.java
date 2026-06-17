package com.tasks.application.port.input;

import com.tasks.domain.model.Task;

public interface CreateTaskUseCase {

    Task create(CreateTaskCommand command);

    record CreateTaskCommand(String title, String description) {}
}
