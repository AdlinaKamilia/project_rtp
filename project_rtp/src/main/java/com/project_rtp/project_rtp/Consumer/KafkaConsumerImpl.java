package com.project_rtp.project_rtp.Consumer;


import com.project_rtp.project_rtp.Producer.KafkaMessageController;
import com.project_rtp.project_rtp.telegramBot.newBot;
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

@Service
public class KafkaConsumerImpl
{
    String message;
    @Autowired
    private newBot myB;

    Message msg;
    @KafkaListener(topics = "userCommentsCount", groupId = "pixelpuff")
    public void listen(String message)
    {
        this.message= message;
        
    }
    public void sendMessageToTelegram(String message) {
        System.out.println(message);
        User user = msg.getFrom();
        Long chatId = user.getId();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText("hi");

        try {
            myB.execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public void userSendMessage(Message msg) throws IOException {
        KafkaMessageController getData = new KafkaMessageController();
        getData.getDataFromGithubToTelegram();
        this.msg = msg;
    }

}