package com.renderhub.backend.service;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MessagePublisher {
    
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${RABBITMQ_EXCHANGE_NAME}")
    private String exchange;

    @Value("${RENDER_TASK_ROUTING_KEY}")
    private String routingKey;

    public void send(String message) {
        rabbitTemplate.convertAndSend(exchange, routingKey, message);
    }
}
