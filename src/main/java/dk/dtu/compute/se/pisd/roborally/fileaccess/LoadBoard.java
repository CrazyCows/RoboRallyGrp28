/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.fileaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import dk.dtu.compute.se.pisd.roborally.controller.field.Gear;
import dk.dtu.compute.se.pisd.roborally.controller.field.LaserGun;
import dk.dtu.compute.se.pisd.roborally.controller.item.Laserbeam;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.BoardTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.PlayerTemplate;
import dk.dtu.compute.se.pisd.roborally.fileaccess.model.SpaceTemplate;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Item;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

import java.io.*;

/**
 * ...
 * Used to load and save a board using JSON with Google's GSON library.
 * It uses both the 'ressources' folder and the 'Save Games' folder
 * to store and retrieve game files.
 *
 */
public class LoadBoard {

    private static final String BOARDSFOLDER = "boards";
    private static final String DEFAULTBOARD = "defaultboard";
    private static final String JSON_EXT = "json";

    public static Board loadBoard(String name, boolean newGame) {
        if (name == null) {
            name = DEFAULTBOARD;
        } else if (name.equals("empty")) {
            return new Board(8, 8);
        }

        ClassLoader classLoader;
        InputStream inputStream = null;

        JsonReader reader = null;
        try {
            if (newGame) {
                classLoader = LoadBoard.class.getClassLoader();
                inputStream = classLoader.getResourceAsStream(BOARDSFOLDER + "/" + name + "." + JSON_EXT);
            } else {
                inputStream = new FileInputStream("Save Games/" + name + "/board.json");
            }

            if (inputStream == null) {
                // TODO these constants should be defined somewhere
                return new Board(8, 8);
            }


            // In simple cases, we can create a Gson object with new Gson():
            GsonBuilder simpleBuilder = new GsonBuilder()
                    .registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>());
            Gson gson = simpleBuilder.create();

            Board result;
            // FileReader fileReader = null;
            reader = null;
            // fileReader = new FileReader(filename);
            reader = gson.newJsonReader(new InputStreamReader(inputStream));
            BoardTemplate template = gson.fromJson(reader, BoardTemplate.class);

            result = new Board(template.width, template.height);
            for (SpaceTemplate spaceTemplate : template.spaces) {
                Space space = result.getSpace(spaceTemplate.x, spaceTemplate.y);
                if (space != null) {
                    space.getActions().addAll(spaceTemplate.actions);
                    for (FieldAction fieldAction : space.getActions()) {
                        if (fieldAction instanceof LaserGun) {
                            ((LaserGun) fieldAction).setup(space);
                        }
                    }
                    space.getWalls().addAll(spaceTemplate.walls);
                    space.getBackground().addAll(spaceTemplate.background);
                    space.getItems().addAll(spaceTemplate.items);

                    if (!space.getItems().isEmpty()) {
                        for (Item item : space.getItems()) {
                            item.createEvent();
                        }
                    }

                }
            }
            // TODO: EXPERIMENTAL - uses new PlayerTemplate - see players.txt
            if (!newGame) {
                int it = 0;
                for (PlayerTemplate playerTemplate : template.players) {
                    Player player = result.getPlayer(it);
                    if (player != null) {
                        player.setName(playerTemplate.name);
                        player.setColor(playerTemplate.color);
                        player.setHeading(playerTemplate.heading);
                        player.setSpace(playerTemplate.x, playerTemplate.y);
                    }
                    it += 1;
                }
            }
            reader.close();
            System.out.println(result.getSpace(1, 1).getActions());
            System.out.println(result.getSpace(1, 1).getWalls());
            System.out.println(result.getSpace(1, 1).getPlayer());
            System.out.println(result.getSpace(1, 1).getBackground());
            return result;
        } catch (IOException e1) {
            if (reader != null) {
                try {
                    reader.close();
                    inputStream = null;
                } catch (IOException e2) {
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e2) {
                }
            }
        }
        return null;
    }

    public static void saveBoard(Board board, String name) {
        BoardTemplate template = new BoardTemplate();
        template.width = board.width;
        template.height = board.height;

        for (int i=0; i<board.width; i++) {
            for (int j=0; j<board.height; j++) {
                Space space = board.getSpace(i,j);
                if (!space.getWalls().isEmpty() || !space.getActions().isEmpty() || !space.getItems().isEmpty()) {
                    SpaceTemplate spaceTemplate = new SpaceTemplate();
                    spaceTemplate.x = space.x;
                    spaceTemplate.y = space.y;
                    spaceTemplate.actions.addAll(space.getActions());
                    spaceTemplate.walls.addAll(space.getWalls());
                    spaceTemplate.background.addAll(space.getBackground());
                    spaceTemplate.items.addAll(space.getItems());
                    template.spaces.add(spaceTemplate);
                }
            }
        }

        File folder = new File("Save Games/" + name);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filename = "Save Games" + "/" + name + "/"  + "board" + "." + JSON_EXT;

        // In simple cases, we can create a Gson object with new:
        //
        //   Gson gson = new Gson();
        //
        // But, if you need to configure it, it is better to create it from
        // a builder (here, we want to configure the JSON serialisation with
        // a pretty printer):
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(FieldAction.class, new Adapter<FieldAction>()).
                setPrettyPrinting();
        Gson gson = simpleBuilder.create();

        FileWriter fileWriter = null;
        JsonWriter writer = null;

        try {
            fileWriter = new FileWriter(filename);
            writer = gson.newJsonWriter(fileWriter);
            gson.toJson(template, template.getClass(), writer);
            writer.close();
        } catch (IOException e1) {
            if (writer != null) {
                try {
                    writer.close();
                    fileWriter = null;
                } catch (IOException e2) {}
            }
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e2) {
                    System.out.println(e1.getMessage());
                }
            }
        }
    }
}
