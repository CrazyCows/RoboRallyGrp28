package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.File;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.io.IOException;
import java.util.HashMap;

public class ClientController {
    private HttpClient client;
    private String baseUrl;
    ObjectMapper objectMapper;
    String path;

    HashMap<String, String> jsonID = new HashMap<>();
    String ID;

    public ClientController() {
        this.client = HttpClient.newHttpClient();
        this.baseUrl = baseUrl = "http://localhost:8080";
        this.objectMapper = new ObjectMapper();
        this.path = "data";
        this.jsonID.put("sharedBoard.json", "/jsonBoard?ID=");
        this.jsonID.put("sharedPlayers.json", "/jsonPlayers?ID=");
        this.jsonID.put("cardSequenceRequest.json", "/jsonMoves?ID=");

    }

    public boolean getJSON(String ID, String jsonName) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + jsonID.get(jsonName) + ID))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + response.statusCode());
            }


            String responseJson = response.body();
            JsonNode jsonNode = objectMapper.readTree(responseJson);
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(path, jsonName), jsonNode);
            return true;
        } catch (RuntimeException | IOException | InterruptedException  e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean createJSON(String ID, String jsonName) {
        try {
            File file = new File(path, jsonName);
            JsonNode json = objectMapper.readTree(file);

            WebClient webClient = WebClient.create();
            Mono<String> response = webClient.post()
                    .uri(baseUrl + jsonID.get(jsonName) + ID)
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
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean updateJSON(String ID, String jsonName) {
        try {
            WebClient webClient = WebClient.create();
            File file = new File(path, jsonName);
            JsonNode json = objectMapper.readTree(file);


            Mono<String> response = webClient.put()
                    .uri(baseUrl + jsonID.get(jsonName) + ID)
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
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean deleteJSON(String ID, String jsonName) {
        try {
            // Create the DELETE request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + jsonID.get(jsonName) + ID))
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
        } catch (RuntimeException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }


}