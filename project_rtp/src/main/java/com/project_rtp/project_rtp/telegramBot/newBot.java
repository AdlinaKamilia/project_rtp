package com.project_rtp.project_rtp.telegramBot;

import com.project_rtp.project_rtp.Consumer.KafkaConsumerImpl;
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

@Component
public class newBot extends TelegramLongPollingBot {


    private static final String TELEGRAM_BOT_TOKEN = "6825202901:AAH8EuiNodFL_oo6b6djNvq-rpaPEhVKG-c"; // Replace with your Telegram bot token


    /*public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        newBot bot = new newBot(kafkaConsumer);
        botsApi.registerBot(bot);
    }*/
    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            Long userId = message.getFrom().getId();
            // Handle or log the user ID as needed
            System.out.println("Received message from user ID: " + userId);
            KafkaConsumerImpl userC = new KafkaConsumerImpl();

            // Rest of your logic here...
        }
    }

    @Override
    public String getBotUsername() {
        return "githtel_bot";
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
}
