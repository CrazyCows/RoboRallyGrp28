package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ChatLoader {

    private ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String, Object> mappedMessage = new HashMap<>();
    File file;

    public void packageMessage(String name, String message) {

        mappedMessage.put("name", name);
        mappedMessage.put("message", message);

        file = new File("data", "playerMessage.json");

        try {
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(file, mappedMessage);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
