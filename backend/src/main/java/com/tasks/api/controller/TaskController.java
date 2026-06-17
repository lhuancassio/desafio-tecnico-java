package com.tasks.api.controller;

import com.tasks.api.dto.CreateTaskRequest;
import com.tasks.api.dto.TaskResponse;
import com.tasks.api.dto.UpdateTaskRequest;
import com.tasks.application.port.input.CreateTaskUseCase;
import com.tasks.application.port.input.DeleteTaskUseCase;
import com.tasks.application.port.input.ListTasksUseCase;
import com.tasks.application.port.input.UpdateTaskUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final CreateTaskUseCase createTaskUseCase;
    private final ListTasksUseCase listTasksUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;

    public TaskController(
        CreateTaskUseCase createTaskUseCase,
        ListTasksUseCase listTasksUseCase,
        UpdateTaskUseCase updateTaskUseCase,
        DeleteTaskUseCase deleteTaskUseCase
    ) {
        this.createTaskUseCase = createTaskUseCase;
        this.listTasksUseCase = listTasksUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskResponse create(@Valid @RequestBody CreateTaskRequest request) {
        var task = createTaskUseCase.create(
            new CreateTaskUseCase.CreateTaskCommand(request.title(), request.description())
        );
        return TaskResponse.from(task);
    }

    @GetMapping
    public List<TaskResponse> listAll() {
        return listTasksUseCase.listAll().stream()
            .map(TaskResponse::from)
            .toList();
    }

    @PutMapping("/{id}")
    public TaskResponse update(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request) {
        var task = updateTaskUseCase.update(
            id,
            new UpdateTaskUseCase.UpdateTaskCommand(request.title(), request.description(), request.status())
        );
        return TaskResponse.from(task);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        deleteTaskUseCase.delete(id);
    }
}
