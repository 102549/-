package com.baymax.exam.center.service;

import com.baymax.exam.center.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ExamMessageService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.enabled:false}")
    private boolean rabbitMQEnabled;

    public void sendCheatAlertMessage(Object message) {
        if (!rabbitMQEnabled) {
            log.warn("RabbitMQ is disabled, skipping cheat alert message");
            return;
        }
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.CHEAT_ALERT_EXCHANGE,
                    RabbitMQConfig.CHEAT_ALERT_ROUTING_KEY,
                    message
            );
            log.info("Cheat alert message sent successfully");
        } catch (Exception e) {
            log.error("Failed to send cheat alert message: {}", e.getMessage());
        }
    }

    public void sendGradingMessage(Object message) {
        if (!rabbitMQEnabled) {
            log.warn("RabbitMQ is disabled, skipping grading message");
            return;
        }
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.GRADING_EXCHANGE,
                    RabbitMQConfig.GRADING_ROUTING_KEY,
                    message
            );
            log.info("Grading message sent successfully");
        } catch (Exception e) {
            log.error("Failed to send grading message: {}", e.getMessage());
        }
    }

    public void sendExamMessage(Object message) {
        if (!rabbitMQEnabled) {
            log.warn("RabbitMQ is disabled, skipping exam message");
            return;
        }
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXAM_EXCHANGE,
                    RabbitMQConfig.EXAM_ROUTING_KEY,
                    message
            );
            log.info("Exam message sent successfully");
        } catch (Exception e) {
            log.error("Failed to send exam message: {}", e.getMessage());
        }
    }
}