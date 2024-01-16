package com.project_rtp.project_rtp.Consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerImpl2
{
    @KafkaListener(topics = "wordsCount", groupId = "pixelpuff")
    public void listen(String message)
    {
    }
}