package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ClientController {
    private HttpClient client;
    private String baseUrl;
    ObjectMapper objectMapper;
    String path;

    HashMap<String, String> jsonID = new HashMap<>();
    String ID;

    // TODO: All exceptions is handled rather lazily here. Should be tightened up such errors give useful information..
    // TODO: Throwing stuff is more fun tho..

    public ClientController(boolean online, String ID) {
        this.client = HttpClient.newHttpClient();
        if (online) {
            this.baseUrl = "http://20.86.101.206:80";
        }
        else {
            this.baseUrl = "http://localhost:8080";
        }
        this.objectMapper = new ObjectMapper();
        this.path = "data";
        this.ID = ID;
    }

    public ClientController() {
        this.client = HttpClient.newHttpClient();
        //this.baseUrl = "http://20.86.101.206:80";
        this.baseUrl = "http://localhost:8080";
        this.objectMapper = new ObjectMapper();
        this.path = "data";
    }


    public synchronized String jsonType(String jsonName){
        if (jsonName.equals("playerData.json")){
            return "/jsonPlayer?ID=";
        } else if (jsonName.equals("cardSequenceRequest.json")){
            return "/jsonCardSequence?ID=";  // TODO: JEG HAR LAVET NOGET OM HER
        }
        else if (jsonName.equals("playerMessage.json")) {
            return "/jsonChat?ID=";
        }
         else {
            return "/jsonHandler?ID=";
        }
    }

    public synchronized void getJSON(String jsonName) {
        String jsonTypeToURL = jsonType(jsonName);
        String childName = "";

        if (jsonName.equals("playerData.json")) {
            childName = "collectivePlayerData.json";
        } else if (jsonName.equals("cardSequenceRequest.json")) {
            childName = "cardSequenceRequests.json";
        }
        if (jsonName.equals("playerMessage.json")) {
            childName = "collectivePlayerMessages.json";
        }
        if  (jsonName.equals("sharedBoard.json")) {
            childName = "sharedBoard.json";
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + jsonTypeToURL + this.ID + "&=" + jsonName))
                    .GET()
                    .build();

            //TODO: Catch ConnectException and stuff
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Failed : HTTP error code : " + response.statusCode());
            }

            String responseJson = response.body();
            JsonNode jsonNode = objectMapper.readTree(responseJson);
            boolean access;
            do {
                access = AccessDataFile.requestFileAccess(childName);
                if (access) {
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, childName), jsonNode);
                    AccessDataFile.releaseFileAccess(childName);
                }
                else {
                    Thread.sleep(50);
                }
            } while(!access);
        } catch (IOException | InterruptedException | RuntimeException e) {
            e.printStackTrace();
        }
    }


    public synchronized void createJSON(String jsonName) {
        System.out.println("Loading. Please wait.");
        String jsonTypeToURL = jsonType(jsonName);
        try {
            File file = new File(path, jsonName);
            JsonNode json = objectMapper.readTree(file);

            WebClient webClient = WebClient.create();
            Mono<String> response = webClient.post()
                    .uri(baseUrl + jsonTypeToURL + this.ID + "&jsonFileName=" + jsonName)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(json)
                    .retrieve()
                    // Error handling
                    // 409 returns File conflict on server - already exists
                    // 500 returns internal error - Could not write to file
                    .onStatus(httpStatus -> httpStatus.is4xxClientError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> {
                                        JsonNode jsonNode;
                                        try {
                                            jsonNode = new ObjectMapper().readTree(body);
                                            String status = jsonNode.get("status").asText();
                                            String error = jsonNode.get("error").asText();
                                            String path = jsonNode.get("path").asText();
                                            return new RuntimeException("Status: " + status + ", Error: " + error + ", Path: " + path);
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }))
                    .onStatus(httpStatus -> httpStatus.is5xxServerError(), clientResponse ->
                            clientResponse.bodyToMono(String.class)
                                    .map(body -> {
                                        JsonNode jsonNode;
                                        try {
                                            jsonNode = new ObjectMapper().readTree(body);
                                            String status = jsonNode.get("status").asText();
                                            String error = jsonNode.get("error").asText();
                                            String path = jsonNode.get("path").asText();
                                            return new RuntimeException("Status: " + status + ", Error: " + error + ", Path: " + path);
                                        } catch (JsonProcessingException e) {
                                            e.printStackTrace();
                                        }
                                        return null;
                                    }))
                    .bodyToMono(String.class);

            String responseString;
            try {
                responseString = response.block();
                System.out.println("Success");
                System.out.println(responseString);
            } catch (RuntimeException e) {
                System.out.println("Failure");
                System.out.println(e.getMessage());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void updateJSON(String jsonName) {
        String jsonTypeToURL = jsonType(jsonName);
        try {
            WebClient webClient = WebClient.create();

            boolean access;
            do {
                access = AccessDataFile.requestFileAccess(jsonName);
                if (access) {

                    File file = new File(path, jsonName);
                    JsonNode json = objectMapper.readTree(file);


                    Mono<String> response = webClient.put()
                            .uri(baseUrl + jsonTypeToURL + this.ID + "&jsonFileName=" + jsonName)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(json)
                            .retrieve()
                            .bodyToMono(String.class);

                    String responseString;
                    try {
                        responseString = response.block();
                        System.out.println("Success");
                        System.out.println(responseString);
                    } catch (RuntimeException e) {
                        System.out.println("Failure");
                        System.out.println(e.getMessage());
                    }

                    AccessDataFile.releaseFileAccess(jsonName);
                }
                else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } while(!access);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // Deletes the whole game folder. Individual files should not be deleted.
    public synchronized void deleteJSON() {

        // Create the DELETE request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/jsonHandler?ID=" + this.ID))
                .DELETE()
                .build();
        try {
            // Send the request and get the response
            HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

            // Check the response status code
            if (response.statusCode() == 200) {
                System.out.println("Success");
            } else {
                System.out.println("Fail " + response.statusCode());
            }
        } catch (Exception e) {
            System.out.println("An error occurred while sending the request: " + e.getMessage());
        }
    }

    public synchronized void availableGamesJSON() {;
        String jsonTypeToURL = "/jsonGames";

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + jsonTypeToURL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Failed : HTTP error code : " + response.statusCode());
            }

            String responseJson = response.body();
            JsonNode jsonNode = objectMapper.readTree(responseJson);



            boolean access;
            do {
                access = AccessDataFile.requestFileAccess("retrievedGames.json");
                if (access) {
                    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path + "/retrievedGames.json" ), jsonNode);
                    AccessDataFile.releaseFileAccess("retrievedGames.json");
                }
                else {
                    Thread.sleep(50);
                }
            } while(!access);

        } catch (IOException | InterruptedException | RuntimeException e) {
            e.printStackTrace();
        }


    }

    public static boolean startRestful () {

        // Run the JAR file
        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "RoboRallyRESTful.jar");

        try {
            Process process = processBuilder.start();

            //int exitCode = process.waitFor();

            //System.out.println("Exited with code: " + exitCode);

            //return exitCode == 0;
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


}