package com.tasks.infrastructure.messaging;

import com.tasks.application.port.output.TaskEventPort;
import com.tasks.domain.event.TaskCreatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class TaskEventProducer implements TaskEventPort {

    private static final Logger log = LoggerFactory.getLogger(TaskEventProducer.class);

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.task-created}")
    private String taskCreatedTopic;

    @Value("${kafka.topics.task-updated}")
    private String taskUpdatedTopic;

    public TaskEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publishTaskCreated(TaskCreatedEvent event) {
        log.info("Publishing task.created event: taskId={}, title={}", event.taskId(), event.title());
        kafkaTemplate.send(taskCreatedTopic, event.taskId().toString(), event);
    }

    @Override
    public void publishTaskUpdated(TaskCreatedEvent event) {
        log.info("Publishing task.updated event: taskId={}, status={}", event.taskId(), event.status());
        kafkaTemplate.send(taskUpdatedTopic, event.taskId().toString(), event);
    }
}
