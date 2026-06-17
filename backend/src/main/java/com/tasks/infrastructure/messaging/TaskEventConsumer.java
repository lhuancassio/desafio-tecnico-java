package com.tasks.infrastructure.messaging;

import com.tasks.domain.event.TaskCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TaskEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TaskEventConsumer.class);

    @KafkaListener(topics = "${kafka.topics.task-created}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleTaskCreated(TaskCreatedEvent event) {
        log.info("Event consumed [task.created]: taskId={}, title={}, status={}, occurredAt={}",
            event.taskId(), event.title(), event.status(), event.occurredAt());
    }

    @KafkaListener(topics = "${kafka.topics.task-updated}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleTaskUpdated(TaskCreatedEvent event) {
        log.info("Event consumed [task.updated]: taskId={}, title={}, status={}, occurredAt={}",
            event.taskId(), event.title(), event.status(), event.occurredAt());
    }
}
