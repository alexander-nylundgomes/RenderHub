package com.renderhub.backend.service;

import java.util.HashMap;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class MessageConsumer {

    @Autowired
    private AssetService assetService;
    
    @RabbitListener(queues = "${RENDER_RESPONSE_QUEUE_NAME}")
    public void receiveMessage(String message){
        try {
            System.out.println("Backend received response from worker! " + message);
            ObjectMapper mapper = new ObjectMapper();
            
            @SuppressWarnings("unchecked")
            HashMap<String, Object> objectMessage = mapper.readValue(message, HashMap.class);

            Long assetVersionId = Long.valueOf((Integer) objectMessage.get("assetVersionId"));
            Boolean renderSuccess = (Boolean) objectMessage.get("success");

            assetService.handleAssetVersionFinishedRendered(assetVersionId, renderSuccess);
        } catch (Exception e) {
            System.out.println("Failed handling response from worker!");
        }
    }
}
