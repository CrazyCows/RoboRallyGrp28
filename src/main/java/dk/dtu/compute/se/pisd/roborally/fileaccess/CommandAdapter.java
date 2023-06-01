package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.model.Command;

import java.io.IOException;

public class CommandAdapter extends TypeAdapter<Command> {
    @Override
    public void write(JsonWriter out, Command value) throws IOException {
        // Not needed for serialization
    }

    @Override
    public Command read(JsonReader in) throws IOException {
        String commandName = in.nextString();
        return Command.valueOf(commandName.toUpperCase());
    }
}