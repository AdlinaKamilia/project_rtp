package com.project_rtp.project_rtp.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;



@Service
public class KafkaProducerUserComments {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final static String TOPIC_NAME = "userCommentsCount";

    //send message to Kafka
    public void sendMessage(String message)
    {
        kafkaTemplate.send(TOPIC_NAME, message);
    }

}

