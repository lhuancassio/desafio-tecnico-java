package com.tasks.application.port.input;

import com.tasks.domain.model.Task;

import java.util.List;

public interface ListTasksUseCase {

    List<Task> listAll();
}
