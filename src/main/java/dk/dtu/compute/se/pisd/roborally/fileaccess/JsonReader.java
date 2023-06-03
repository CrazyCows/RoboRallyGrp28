package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonReader {

    // TODO: something


    ObjectMapper objectMapper = new ObjectMapper();


    /**
     * Gets the key value from a jsonFile. The jsonFile must only contain one key value
     * @param jsonFileName
     * @param key
     * @return
     */
    public String jsonReadDataInFile(String jsonFileName, String key) {

    }

    /**
     * This method checks if a value exists in the JSON file and returns it
     * @Param jsonFile
     * @Param the value you wish to find
     * @Param The value you wish to compare to
     */
    public String jsonReadDataInFile(String jsonFileName, String key, String valueToCompare) throws IOException{
        // Read JSON file and convert to list of Person
        JsonNode node = objectMapper.readTree(new File("data", jsonFileName));

        // Check if the node is an array
        if (node.isArray()) {
            // Iterate through array elements
            for (JsonNode element : node) {
                // If the valueToCompare field is the key element, print the value is fo
                if (valueToCompare.equals(element.get(key).asText())) {
                    return element.get(key).asText();
                }
            }
        }
        throw new RuntimeException("Data not found in file");
    }
}
