package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.lang.reflect.InvocationTargetException;

public class Item {
    String name;
    String image;
    Heading heading;
    String eventClassName;
    FieldAction event;

    public Item(String name, String image, Heading heading, String eventClassName) {
        this.name = name;
        this.image = image;
        this.heading = heading;
        this.eventClassName = eventClassName;
    }

    public String getName() {
        return this.name;
    }

    public String getImage() {
        return this.image;
    }

    public FieldAction getEvent() {
        return event;
    }

    public void createEvent() {
        try {
            Class<?> eventClass = Class.forName(this.eventClassName);
            this.event = (FieldAction) eventClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            this.event = null;
        }
    }
}