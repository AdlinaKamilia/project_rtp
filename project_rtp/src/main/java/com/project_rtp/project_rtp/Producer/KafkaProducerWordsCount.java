package com.project_rtp.project_rtp.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerWordsCount {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final static String TOPIC_NAME = "wordsCount";

    public void sendMessage(String message)
    {
        kafkaTemplate.send(TOPIC_NAME, message);
    }

}

