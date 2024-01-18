package com.project_rtp.project_rtp.Consumer;


import com.project_rtp.project_rtp.Producer.KafkaMessageController;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.io.IOException;

@Service
public class KafkaConsumerImpl
{
    String message;
    @KafkaListener(topics = "userCommentsCount", groupId = "pixelpuff")
    public void listen(String message)
    {
    }

    //prompt to get data from GitHub
    public void userSendMessage(Message msg) throws IOException {
        KafkaMessageController getData = new KafkaMessageController();
        getData.getDataFromGithubToTelegram();
    }

}