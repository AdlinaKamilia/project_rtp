package com.project_rtp.project_rtp.telegramBot;

import com.project_rtp.project_rtp.Consumer.KafkaConsumerImpl;
import com.project_rtp.project_rtp.Producer.KafkaMessageController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.LinkedList;

@Component
public class newBot extends TelegramLongPollingBot {


    private static final String TELEGRAM_BOT_TOKEN = "6717304261:AAFke6aufbug7FqpzrMYGd_e13wXtu0MCsM"; // Replace with your Telegram bot token
    //private final KafkaMessageController kafkaMessageController;
    static String format;
    static Long userId;


    /*public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        newBot bot = new newBot(kafkaConsumer);
        botsApi.registerBot(bot);
    }*/
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            userId = message.getFrom().getId();
            // Handle or log the user ID as needed
            System.out.println("Received message from user ID: " + userId);
            KafkaConsumerImpl userC = new KafkaConsumerImpl();
            try {
                userC.userSendMessage(message);
                Thread.sleep(5000);
                sendText(userId, format);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Rest of your logic here...
        }
    }

    @Override
    public String getBotUsername() {
        return "A231_STIW3054_pixelpuff_bot";
    }

    /**
     * This method is to get the Bot Token for API registration.
     *
     * @return The Bot Token.
     */
    @Override
    public String getBotToken() {
        return TELEGRAM_BOT_TOKEN;
    }
    public static void sendToTelegram(LinkedList list){
        format="";
        for (int i = 0; i < list.size(); i++) {
            format = format + "\n" + list.get(i);
            System.out.println(format);
        }
        //sendText(userId, format);

    }
    public void sendText(Long userID, String text){
        SendMessage sm = new SendMessage();
        sm.setChatId(String.valueOf(userID));
        sm.setParseMode(ParseMode.MARKDOWN);
        sm.setText(text);
        try{
            execute(sm);
        }catch (TelegramApiException e){
            throw new RuntimeException(e);
        }
    }
}
