package com.project_rtp.project_rtp.Consumer;


import com.project_rtp.project_rtp.Producer.KafkaMessageController;
import com.project_rtp.project_rtp.telegramBot.newBot;
import jakarta.ws.rs.core.Link;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramBot;

import java.io.IOException;
import java.util.LinkedList;

@Service
public class KafkaConsumerImpl
{
    String message;
    @Autowired
    private newBot myB;
    LinkedList getList;
    @KafkaListener(topics = "userCommentsCount", groupId = "pixelpuff")
    public void listen(String message)
    {
    }

    public void userSendMessage(Message msg) throws IOException {
        KafkaMessageController getData = new KafkaMessageController();
        getData.getDataFromGithubToTelegram();

    }

}