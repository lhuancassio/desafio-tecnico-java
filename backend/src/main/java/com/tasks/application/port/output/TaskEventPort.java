package com.tasks.application.port.output;

import com.tasks.domain.event.TaskCreatedEvent;

public interface TaskEventPort {

    void publishTaskCreated(TaskCreatedEvent event);

    void publishTaskUpdated(TaskCreatedEvent event);
}
