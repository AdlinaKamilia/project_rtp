package com.project_rtp.project_rtp.Producer;

import com.project_rtp.project_rtp.Consumer.KafkaConsumerImpl;
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

    private final Object lockObject = new Object();
    boolean userAvailable = false;


    @Autowired
    private KafkaProducerUserComments kafkaProducerUserComments;

    /*@PostMapping("/publish/{animalName}")
    public String publishMessage(@PathVariable("animalName")final String animalName)
    {
        kafkaProducerImpl.sendMessage(animalName);
        System.out.println("Successfully Published the Animal Name = '" + animalName + "' to the AnimalTopic");
        return "Successfully Published the Animal Name = " + animalName + " to the AnimalTopic";
    }*/
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

    private static String createMessage(int rank, String userLogin, int commentsC) {
        return rank + ". " + userLogin + " [" + commentsC + " comments]";
    }
    private void getGithubData() throws IOException {
        // Create a persistent record of processed user IDs
        Set<String> processedUserIds = new HashSet<>();
        // GitHub API URL
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

            // Parse the JSON response here
            String jsonResponse = response.toString();
            // Process the JSON data
            IssueComment[] comments = gson.fromJson(jsonResponse, (Type) IssueComment[].class);
            // Map to store the count of comments for each user
            Map<String, Integer> commentCountMap = new HashMap<>();

            for (IssueComment comment : comments) {
                String userLogin = comment.getUser().getLogin();
                //String body = comment.getBody();
                if (!processedUserIds.contains(userLogin)) {
                    processedUserIds.add(userLogin);}
                // Update the comment count for the user
                commentCountMap.put(userLogin, commentCountMap.getOrDefault(userLogin, 0) + 1);
            }

            int rank = 1;

            //simpan user array
            LinkedList userCommentCounts = new LinkedList<>();
            for (Map.Entry<String, Integer> entry : commentCountMap.entrySet()) {
                String userLogin = entry.getKey();
                int commentCount = entry.getValue();

                // Create the message using the format from the `createMessage` method
                String message = createMessage(rank,userLogin, commentCount);

                // Send the message to Kafka or perform other actions
                System.out.println(message);

                kafkaProducerUserComments.sendMessage(message);
                if(userAvailable){
                    KafkaConsumerImpl toConsume = new KafkaConsumerImpl();
                    toConsume.sendMessageToTelegram(message);
                }
                userAvailable= false;

                /*// Alternatively, send to Kafka:
                ProducerRecord<String, String> record = new ProducerRecord<>("userCommentsCount", message);
                producer.send(record);*/

                rank++;
            }
        }
    }
    public void getDataFromGithubToTelegram() throws IOException {
        userAvailable=true;
        getGithubData();
        System.out.println("hi1");
    }
    public void setUserAvailable(){
        userAvailable=false;
    }
}
