package com.tasks.infrastructure.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.task-created}")
    private String taskCreatedTopic;

    @Value("${kafka.topics.task-updated}")
    private String taskUpdatedTopic;

    @Bean
    public NewTopic taskCreatedTopic() {
        return TopicBuilder.name(taskCreatedTopic).partitions(1).replicas(1).build();
    }

    @Bean
    public NewTopic taskUpdatedTopic() {
        return TopicBuilder.name(taskUpdatedTopic).partitions(1).replicas(1).build();
    }
}
