package com.project_rtp.project_rtp.Producer;

import com.google.gson.Gson;
import com.project_rtp.project_rtp.telegramBot.TelegramBot;
import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@RestController
public class KafkaMessageController2 {

    private final Object lockObject = new Object();

    boolean userAvailable = false; // user send some message
    LinkedList wordsCount = new LinkedList<>(); // list of content
    @Autowired
    private KafkaProducerWordsCount kafkaProducerWordsCount;
    @PostConstruct
    public void init() {
        fetchDataAsync();
    }

    @Async
    public void fetchDataAsync() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        executorService.execute(() -> {
            try {
                synchronized (lockObject) {
                    getGithubData();
                }
            } catch (IOException e) {
                e.printStackTrace();  // Handle the exception appropriately in your application
            } finally {
                executorService.shutdown();  // Shutdown the executor when the task is completed
            }
        });
    }
    //formatting message to put into list
    private static String createMessage(int rank, String userLogin, int commentsC) {
        return rank + ". " + userLogin + " [" + commentsC + " comments]";
    }
    //fetching the data from GitHub
    private void getGithubData() throws IOException {
        // Get GitHub issues using RestAPI
        String apiUrl = "https://api.github.com/repos/AdlinaKamilia/Project_STIW3044/issues/comments";
        Gson gson = new Gson();
        URL url = new URL(apiUrl);
        // Open a connection to the URL
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        // Set request method
        connection.setRequestMethod("GET");

        // Get the response code
        int responseCode = connection.getResponseCode();

        // Read the response data
        if (responseCode == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            IssueComment[] comments = gson.fromJson(jsonResponse, (Type) IssueComment[].class);
            // Map to store the count of words in comments
            Map<String, Integer> wordCountMap = new HashMap<>();

            for (IssueComment comment : comments) {
                String body = comment.getBody();
                // Update the word count
                String[] words = body.split("\\s+");
                for (String word : words) {
                    // Remove non-alphabetic characters and convert to lowercase
                    word = word.replaceAll("[^a-zA-Z]", "").toLowerCase();
                    if(!word.isEmpty()){
                        wordCountMap.put(word, wordCountMap.getOrDefault(word, 0) + 1);
                    }

                }
            }

            int rank = 1;
            for (Map.Entry<String, Integer> entry : wordCountMap.entrySet()) {
                String word = entry.getKey();
                int commentCount = entry.getValue();

                // Create the message using the format
                String message = createMessage(rank,word, commentCount);

                // Send the message to Kafka or perform other actions
                if (kafkaProducerWordsCount != null) {
                    kafkaProducerWordsCount.sendMessage(message);
                } else if (userAvailable) {
                    wordsCount.add(message);
                }
                rank++;
            }
            // send to Telegram
            if(userAvailable){
                TelegramBot bot = new TelegramBot();
                TelegramBot.sendToTelegram(wordsCount);
            }
        }
    }
    //fetch data and prompt to Telegram
    public void getDataFromGithubToTelegram() throws IOException {
        userAvailable=true;
        fetchDataAsync();
    }
}
