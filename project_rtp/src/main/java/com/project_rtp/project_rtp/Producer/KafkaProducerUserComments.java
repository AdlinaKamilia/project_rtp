package com.project_rtp.project_rtp.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Service
public class KafkaProducerUserComments {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private final static String TOPIC_NAME = "userCommentsCount";

    public void sendMessage(String message)
    {
        kafkaTemplate.send(TOPIC_NAME, message);
    }

}

