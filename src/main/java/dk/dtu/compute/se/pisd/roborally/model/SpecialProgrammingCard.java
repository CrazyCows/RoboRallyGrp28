package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.CardAction;

import java.lang.reflect.InvocationTargetException;

public class SpecialProgrammingCard extends Card {

    CardAction<SpecialProgrammingCard> action;

    public SpecialProgrammingCard(String name, String effect, String imagePath, String actionClassName) {
        this.name = name;
        this.effect = effect;
        this.imagePath = imagePath;
        this.actionClassName = actionClassName;

    }

    @SuppressWarnings("unchecked")
    public void createAction() {
        try {
            Class<?> eventClass = Class.forName(this.actionClassName);
            this.action = (CardAction<SpecialProgrammingCard>) eventClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            this.action = null;
        }
    }


    public void setAction(CardAction<SpecialProgrammingCard> action) {
        this.action = action;
    }

    public CardAction<SpecialProgrammingCard> getAction() {
        return this.action;
    }

}
