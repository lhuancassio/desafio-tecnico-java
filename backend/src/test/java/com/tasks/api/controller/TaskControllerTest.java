package com.tasks.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tasks.api.dto.CreateTaskRequest;
import com.tasks.api.dto.UpdateTaskRequest;
import com.tasks.application.port.input.CreateTaskUseCase;
import com.tasks.application.port.input.DeleteTaskUseCase;
import com.tasks.application.port.input.ListTasksUseCase;
import com.tasks.application.port.input.UpdateTaskUseCase;
import com.tasks.domain.exception.TaskNotFoundException;
import com.tasks.domain.model.Task;
import com.tasks.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CreateTaskUseCase createTaskUseCase;

    @MockBean
    private ListTasksUseCase listTasksUseCase;

    @MockBean
    private UpdateTaskUseCase updateTaskUseCase;

    @MockBean
    private DeleteTaskUseCase deleteTaskUseCase;

    @Test
    void shouldCreateTaskAndReturn201() throws Exception {
        var request = new CreateTaskRequest("New Task", "Some description");
        var created = new Task(UUID.randomUUID(), "New Task", "Some description", TaskStatus.PENDING, LocalDateTime.now());

        when(createTaskUseCase.create(any())).thenReturn(created);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.title").value("New Task"))
            .andExpect(jsonPath("$.status").value("PENDING"))
            .andExpect(jsonPath("$.statusDisplayName").value("Pendente"));
    }

    @Test
    void shouldReturn400WhenTitleIsBlank() throws Exception {
        var request = new CreateTaskRequest("", null);

        mockMvc.perform(post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.error").value("Validation failed"));
    }

    @Test
    void shouldListAllTasks() throws Exception {
        var tasks = List.of(
            new Task(UUID.randomUUID(), "Task 1", "Desc 1", TaskStatus.PENDING, LocalDateTime.now()),
            new Task(UUID.randomUUID(), "Task 2", "Desc 2", TaskStatus.IN_PROGRESS, LocalDateTime.now())
        );

        when(listTasksUseCase.listAll()).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Task 1"))
            .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));
    }

    @Test
    void shouldReturn404WhenTaskNotFound() throws Exception {
        var id = UUID.randomUUID();
        var request = new UpdateTaskRequest("Title", null, TaskStatus.DONE);

        when(updateTaskUseCase.update(eq(id), any())).thenThrow(new TaskNotFoundException(id));

        mockMvc.perform(put("/api/tasks/" + id)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void shouldDeleteTaskAndReturn204() throws Exception {
        var id = UUID.randomUUID();
        doNothing().when(deleteTaskUseCase).delete(id);

        mockMvc.perform(delete("/api/tasks/" + id))
            .andExpect(status().isNoContent());
    }
}
