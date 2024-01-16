package com.project_rtp.project_rtp.Consumer;


import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerImpl
{
    @KafkaListener(topics = "userCommentsCount", groupId = "pixelpuff")
    public void listen(String message)
    {
    }
}