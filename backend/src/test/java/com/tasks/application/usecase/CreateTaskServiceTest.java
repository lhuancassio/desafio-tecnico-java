package com.tasks.application.usecase;

import com.tasks.application.port.input.CreateTaskUseCase;
import com.tasks.application.port.output.TaskEventPort;
import com.tasks.application.port.output.TaskRepositoryPort;
import com.tasks.domain.event.TaskCreatedEvent;
import com.tasks.domain.model.Task;
import com.tasks.domain.model.TaskStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateTaskServiceTest {

    @Mock
    private TaskRepositoryPort taskRepositoryPort;

    @Mock
    private TaskEventPort taskEventPort;

    @InjectMocks
    private CreateTaskService createTaskService;

    @Test
    void shouldCreateTaskWithPendingStatus() {
        var command = new CreateTaskUseCase.CreateTaskCommand("Test Task", "Test description");
        var savedTask = new Task(UUID.randomUUID(), "Test Task", "Test description", TaskStatus.PENDING, LocalDateTime.now());

        when(taskRepositoryPort.save(any(Task.class))).thenReturn(savedTask);

        var result = createTaskService.create(command);

        assertThat(result.getTitle()).isEqualTo("Test Task");
        assertThat(result.getStatus()).isEqualTo(TaskStatus.PENDING);
    }

    @Test
    void shouldSaveTaskWithPendingStatusAndPublishEvent() {
        var command = new CreateTaskUseCase.CreateTaskCommand("My Task", null);
        var savedTask = new Task(UUID.randomUUID(), "My Task", null, TaskStatus.PENDING, LocalDateTime.now());

        when(taskRepositoryPort.save(any(Task.class))).thenReturn(savedTask);

        createTaskService.create(command);

        var taskCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepositoryPort).save(taskCaptor.capture());
        assertThat(taskCaptor.getValue().getStatus()).isEqualTo(TaskStatus.PENDING);
        assertThat(taskCaptor.getValue().getId()).isNull();

        verify(taskEventPort).publishTaskCreated(any(TaskCreatedEvent.class));
    }

    @Test
    void shouldNotPublishEventWhenSaveFails() {
        var command = new CreateTaskUseCase.CreateTaskCommand("Fail Task", null);

        when(taskRepositoryPort.save(any(Task.class))).thenThrow(new RuntimeException("DB error"));

        org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
            () -> createTaskService.create(command));

        verifyNoInteractions(taskEventPort);
    }
}
