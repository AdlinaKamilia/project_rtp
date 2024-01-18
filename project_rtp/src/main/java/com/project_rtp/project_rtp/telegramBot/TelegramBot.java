
package com.project_rtp.project_rtp.telegramBot;

import com.project_rtp.project_rtp.Consumer.KafkaConsumerImpl;
import com.project_rtp.project_rtp.Consumer.KafkaConsumerImpl2;
import com.project_rtp.project_rtp.ProjectRtpApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.io.IOException;
import java.util.LinkedList;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    private static final String TELEGRAM_BOT_TOKEN = "6717304261:AAFke6aufbug7FqpzrMYGd_e13wXtu0MCsM";
    static String format;
    static Long userId,userO;
    boolean inputRequested;
    private final Object lock= new Object();
    private static Thread processingThread = null, inactivityCheckerThread;

    private static  final long INACTIVITY_TIMEOUT = 30000;
    private Long lastActivityTime;


    @Override
    public void onUpdateReceived(Update update) {
        StringBuilder outputMessage = new StringBuilder();
        outputMessage.append("```\n");
        Message message = update.getMessage();
        userId = message.getFrom().getId();

        if (message.hasText()) {
            String userText = message.getText().toLowerCase();
            System.out.println("Received message from user ID: " +userId);

            if (userText.equals("/start")) {
                sendText(message.getChatId(), "Welcome to pixelpuff bot!\nAvailable commands:\n1. /input - To input your request\n2. /end - To terminate the conversation");
            } else if (userText.equals("/end")) {

                if(processingThread!=null&&userO.equals(userId)){
                    handleEndCommand(userId);
                }else {
                    sendText(userId,"You are not in process on getting GitHub Data. Please try again later.");
                }
            } else if (userText.equals("/input")) {
                synchronized (lock){
                    if (processingThread==null|| userO.equals(userId)){
                        lastActivityTime= System.currentTimeMillis();
                        userO=userId;
                        processingThread= new Thread();
                        processingThread.start();
                        inactivityCheckerThread = new Thread(this:: checkInactivity);
                        inactivityCheckerThread.start();
                        inputRequested = true;
                        sendText(message.getChatId(), "Choose an option:\n1 - Request user comments count\n2 - Request words count for all comments\n3 - Request both user comments and words count for all comments");
                    }else {
                        sendText(message.getFrom().getId(),"The Fetching Data process is occupied. Please wait for a few minutes...");
                    }
                }

            } else if (inputRequested) {
                if (userO.equals(userId)) {

                    synchronized (lock) {
                        lastActivityTime= System.currentTimeMillis();
                        if(userText.equals("1")||userText.equals("2")||userText.equals("3")){
                            KafkaConsumerImpl userC = new KafkaConsumerImpl();
                            KafkaConsumerImpl2 wordsC = new KafkaConsumerImpl2();
                            ConfigurableApplicationContext context = SpringApplication.run(ProjectRtpApplication.class);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                            try {
                                if(userText.equals("1")){
                                    LinkedList userCList = userC.getList();
                                    sendToTelegram(userCList);
                                    sendText(userId, "Fetching the Data From Github");
                                    Thread.sleep(2000);
                                    outputMessage.append(String.format("%20s\n","User's comments count"));
                                    outputMessage.append(format);
                                    outputMessage.append("```");
                                    sendText(userId, outputMessage.toString());
                                    SpringApplication.exit(context);
                                    userCList.clear();
                                }else if (userText.equals("2")) {
                                    LinkedList wordsCList = wordsC.getList();
                                    sendToTelegram(wordsCList);
                                    sendText(userId, "Fetching the Data From Github");
                                    Thread.sleep(2000);
                                    outputMessage.append(String.format("%10s\n","Words count"));
                                    outputMessage.append(format);
                                    outputMessage.append("```");
                                    sendText(userId, outputMessage.toString());
                                    SpringApplication.exit(context);
                                    wordsCList.clear();
                                }else if(userText.equals("3")){
                                    LinkedList userCList = userC.getList();
                                    sendToTelegram(userCList);
                                    sendText(userId, "Fetching the Data From Github");
                                    Thread.sleep(2000);
                                    outputMessage.append(String.format("%20s\n","User's comments count"));
                                    outputMessage.append(format);
                                    outputMessage.append("```");
                                    sendText(userId, outputMessage.toString());
                                    Thread.sleep(2000);
                                    outputMessage= new StringBuilder();
                                    outputMessage.append("```\n");
                                    outputMessage.append(String.format("%10s\n","Words count"));
                                    LinkedList wordsCList = wordsC.getList();
                                    sendToTelegram(wordsCList);
                                    Thread.sleep(2000);
                                    outputMessage.append(format);
                                    outputMessage.append("```");
                                    sendText(userId, outputMessage.toString());
                                    SpringApplication.exit(context);
                                    userCList.clear();
                                    wordsCList.clear();
                                }
                                sendText(userO, "Process Finished. \nFor another fetch of data, please use this command again \"/input\".\nTo end the conversation please use this command \"/end\".");
                                inputRequested= false;

                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }
                        }else {
                            // Invalid input after /input, display error message
                            sendText(userO, "Invalid request. Please input 1, 2, or 3 only.");
                        }


                    }


                }
            } else {
                // Invalid input before /input, display error message
                sendText(message.getChatId(), "Invalid request. Available commands:\n1. /input - To input your request\n2. /end - To terminate the conversation");
            }
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

    //method to get the data
    public static void sendToTelegram(LinkedList list) {
        format = "";
        for (int i = 0; i < list.size(); i++) {
            format = format + "\n" + list.get(i);
            System.out.println(list.get(i));
        }

    }

    //method for sendText
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
    private void checkInactivity(){
        while(true){
            try{
                Thread.sleep(INACTIVITY_TIMEOUT);
                synchronized (lock){
                    if (System.currentTimeMillis()-lastActivityTime> INACTIVITY_TIMEOUT&&processingThread!=null){
                        processingThread.interrupt();
                        System.out.println("The input process in terminated");
                        sendText(userO, "You did not reply in 30 seconds, the process is terminated. Please try again later....");
                        handleEndCommand(userO);
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Inactivity checker thread interrupted.");
                throw new RuntimeException(e);
            }
        }
    }
    private void handleEndCommand(Long userId) {
        synchronized (lock){
            if(processingThread != null && processingThread.isAlive()){
                processingThread.interrupt();
                processingThread= null;
                userO=null;
                inputRequested=false;
            }
        }

        if (userO.equals(userId)){
            processingThread=null;
            userO=null;
            inputRequested= false;
        }

        sendText(userId,"Goodbye! The conversation has ended.");
    }
}