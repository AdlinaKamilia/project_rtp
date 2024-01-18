package com.project_rtp.project_rtp.Consumer;


import com.project_rtp.project_rtp.Producer.KafkaMessageController2;
import com.project_rtp.project_rtp.ProjectRtpApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.io.IOException;
import java.util.LinkedList;

@Service
public class KafkaConsumerImpl2
{
    static  LinkedList<Object> list = new LinkedList<>();
    @KafkaListener(topics = "wordsCount", groupId = "pixelpuff")
    public void listen(String message)
    {
        list.add(message);
    }
    //prompt to get data from GitHub
    public void userSendMessage(Message msg) throws IOException {
        KafkaMessageController2 getData = new KafkaMessageController2();
        getData.getDataFromGithubToTelegram();
    }
    public LinkedList getList(){
        return  list;
    }
}