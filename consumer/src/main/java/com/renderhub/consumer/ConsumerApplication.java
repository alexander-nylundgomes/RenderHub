package com.renderhub.consumer;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

public class ConsumerApplication {

    // Render task
    private static final String RENDER_TASK_QUEUE_NAME = System.getenv("RENDER_TASK_QUEUE_NAME");
    private static final String RENDER_TASK_ROUTING_KEY = System.getenv("RENDER_TASK_ROUTING_KEY");
    
    // Render response
    private static final String RENDER_RESPONSE_QUEUE_NAME = System.getenv("RENDER_RESPONSE_QUEUE_NAME");
    private static final String RENDER_RESPONSE_ROUTING_KEY = System.getenv("RENDER_RESPONSE_ROUTING_KEY");

    // RabbitMQ
    private static final String USERNAME = System.getenv("RABBITMQ_DEFAULT_USER");
    private static final String PASSWORD = System.getenv("RABBITMQ_DEFAULT_PASS");
    private static final String HOST = System.getenv("RABBITMQ_HOST");
    private static final String EXCHANGE_NAME = System.getenv("RABBITMQ_EXCHANGE_NAME");
    private static final int PORT = Integer.parseInt(System.getenv("RABBITMQ_PORT"));
    private static final String VHOST = "/";

    public static void main(String[] args) {
        Connection connection;
        Channel channel;

        try {
            connection = createConnection(); 
            channel = connection.createChannel();

            configureRabbitMQ(channel);

            System.out.println("Consumer started successfully, listening for messages...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), StandardCharsets.UTF_8);
                System.out.println("Received message: " + message);

                HashMap<String, Object> instructions = convertMessageString(message);
                boolean renderSuccess = handleRenderTask(instructions);

                sendResponse(channel, renderSuccess, instructions);
            };

            channel.basicConsume(RENDER_TASK_QUEUE_NAME, true, deliverCallback, consumerTag -> {
                System.out.println("Consumer cancelled");
            });

        } catch (Exception e) {
            System.err.println("Failed to start consumer.");
            e.printStackTrace();
        }
    }

    private static Connection createConnection() throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setUsername(USERNAME);
        factory.setPassword(PASSWORD);
        factory.setHost(HOST);
        factory.setPort(PORT);
        factory.setVirtualHost(VHOST);
        return factory.newConnection();
    }

    private static void configureRabbitMQ(Channel channel) throws Exception {
        // Declare the exchange and queues
        channel.queueDeclare(RENDER_TASK_QUEUE_NAME, false, false, false, null);
        channel.queueDeclare(RENDER_RESPONSE_QUEUE_NAME, false, false, false, null);

        // Bind the queue to the exchange
        channel.queueBind(RENDER_TASK_QUEUE_NAME, EXCHANGE_NAME, RENDER_TASK_ROUTING_KEY);
        channel.queueBind(RENDER_RESPONSE_QUEUE_NAME, EXCHANGE_NAME, RENDER_RESPONSE_ROUTING_KEY);
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, Object> convertMessageString(String message) throws JsonMappingException, JsonProcessingException{
        ObjectMapper mapper = new ObjectMapper();
        HashMap<String, Object> objectMessage = mapper.readValue(message, HashMap.class);

        return objectMessage;
    }

    private static boolean handleRenderTask(HashMap<String, Object> instructions) {
        Worker worker = new Worker(instructions);
        boolean success = worker.run();

        if (success) {
            System.out.println("Render completed successfully.");
        } else {
            System.err.println("Render failed.");
        }

        return success;
    }

    private static void sendResponse(Channel channel, boolean renderSuccess, HashMap<String, Object> instructions) {
        try {
            HashMap<String, Object> response = new HashMap<>();
            response.put("success", renderSuccess);
            response.put("assetVersionId", (Integer) instructions.get("assetVersionId"));

            ObjectMapper mapper = new ObjectMapper();
            String responseMessage = mapper.writeValueAsString(response);

            channel.basicPublish(EXCHANGE_NAME, RENDER_RESPONSE_ROUTING_KEY, null, responseMessage.getBytes(StandardCharsets.UTF_8));
            System.out.println("Response message sent: " + responseMessage);

        } catch (Exception e) {
            System.err.println("Failed to send response.");
            e.printStackTrace();
        }
    }
}
