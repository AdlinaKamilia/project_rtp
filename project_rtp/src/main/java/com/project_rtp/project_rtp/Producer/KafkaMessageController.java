package com.project_rtp.project_rtp.Producer;

import com.project_rtp.project_rtp.telegramBot.TelegramBot;
import org.springframework.scheduling.annotation.Async;
import com.google.gson.Gson;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;


@RestController
public class KafkaMessageController {

    private final Object lockObject = new Object(); //for locking synchronization
    boolean userAvailable = false; // user send some message
    LinkedList userCommentCounts = new LinkedList<>(); // list of content

    @Autowired
    private KafkaProducerUserComments kafkaProducerUserComments;

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
                e.printStackTrace();
            } finally {
                executorService.shutdown();
            }
        });
    }

    //formatting message to put into list
    private static String createMessage(int rank, String userLogin, int commentsC) {
        return rank + ". " + userLogin + " [" + commentsC + " comments]";
    }

    //fetching the data from GitHub
    private void getGithubData() throws IOException {
        // Information of userId
        Set<String> processedUserIds = new HashSet<>();
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
            // Map to store the count of comments for each user
            Map<String, Integer> commentCountMap = new HashMap<>();

            for (IssueComment comment : comments) {
                String userLogin = comment.getUser().getLogin();
                //check whether the user comments are already in list
                if (!processedUserIds.contains(userLogin)) {
                    processedUserIds.add(userLogin);
                }
                // Update the comment count for the user
                commentCountMap.put(userLogin, commentCountMap.getOrDefault(userLogin, 0) + 1);
            }

            int rank = 1;


            for (Map.Entry<String, Integer> entry : commentCountMap.entrySet()) {
                String userLogin = entry.getKey();
                int commentCount = entry.getValue();

                // Create the message using the format
                String message = createMessage(rank,userLogin, commentCount);

                // Send the message to Kafka or perform other actions
                if (kafkaProducerUserComments != null) {
                    kafkaProducerUserComments.sendMessage(message);
                } else if (userAvailable) {
                    userCommentCounts.add(message);
                }
                rank++;
            }
            // send to Telegram
            if(userAvailable){
                TelegramBot bot = new TelegramBot();
                TelegramBot.sendToTelegram(userCommentCounts);
            }
        }
    }

    //fetch data and prompt to Telegram
    public void getDataFromGithubToTelegram() throws IOException {
        userAvailable=true;
        fetchDataAsync();
    }
}
