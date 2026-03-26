package com.baymax.exam.center.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXAM_EXCHANGE = "exam.exchange";
    public static final String EXAM_QUEUE = "exam.queue";
    public static final String EXAM_ROUTING_KEY = "exam.routing.key";

    public static final String CHEAT_ALERT_EXCHANGE = "cheat.alert.exchange";
    public static final String CHEAT_ALERT_QUEUE = "cheat.alert.queue";
    public static final String CHEAT_ALERT_ROUTING_KEY = "cheat.alert.routing.key";

    public static final String GRADING_EXCHANGE = "grading.exchange";
    public static final String GRADING_QUEUE = "grading.queue";
    public static final String GRADING_ROUTING_KEY = "grading.routing.key";

    @Value("${spring.rabbitmq.enabled:false}")
    private boolean rabbitMQEnabled;

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public DirectExchange examExchange() {
        return new DirectExchange(EXAM_EXCHANGE);
    }

    @Bean
    public Queue examQueue() {
        return QueueBuilder.durable(EXAM_QUEUE).build();
    }

    @Bean
    public Binding examBinding(Queue examQueue, DirectExchange examExchange) {
        return BindingBuilder.bind(examQueue).to(examExchange).with(EXAM_ROUTING_KEY);
    }

    @Bean
    public DirectExchange cheatAlertExchange() {
        return new DirectExchange(CHEAT_ALERT_EXCHANGE);
    }

    @Bean
    public Queue cheatAlertQueue() {
        return QueueBuilder.durable(CHEAT_ALERT_QUEUE).build();
    }

    @Bean
    public Binding cheatAlertBinding(Queue cheatAlertQueue, DirectExchange cheatAlertExchange) {
        return BindingBuilder.bind(cheatAlertQueue).to(cheatAlertExchange).with(CHEAT_ALERT_ROUTING_KEY);
    }

    @Bean
    public DirectExchange gradingExchange() {
        return new DirectExchange(GRADING_EXCHANGE);
    }

    @Bean
    public Queue gradingQueue() {
        return QueueBuilder.durable(GRADING_QUEUE).build();
    }

    @Bean
    public Binding gradingBinding(Queue gradingQueue, DirectExchange gradingExchange) {
        return BindingBuilder.bind(gradingQueue).to(gradingExchange).with(GRADING_ROUTING_KEY);
    }
}