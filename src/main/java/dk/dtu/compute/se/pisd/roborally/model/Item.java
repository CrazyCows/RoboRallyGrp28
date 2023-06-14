package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.lang.reflect.InvocationTargetException;

/*
    This is the model of an item. It defines an item
    with its given action.
    An item can be placed on a space, and is used by
    the Space class, which can hold any number of items.

 */

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


    /**
     *
     * @param name
     * @param image
     * @param heading
     * @param event
     */
    public Item(String name, String image, Heading heading, FieldAction event) {
        this.name = name;
        this.image = image;
        this.heading = heading;
        this.event = event;
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

    public Heading getHeading() {
        return heading;
    }


    // A method that converts the eventClassName string
    // to an actual event. This method should only be used once
    // when the items are created in the LoadBoard class
    public void createEvent() {
        if (this.event != null) {
            return;
        }
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