package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.CardAction;

import java.lang.reflect.InvocationTargetException;

public class DamageCard extends Card {

    CardAction<DamageCard> action;

    public DamageCard(String name, String effect, String imagePath, String actionClassName) {
        this.name = name;
        this.effect = effect;
        this.imagePath = imagePath;
        this.actionClassName = actionClassName;

    }

    @SuppressWarnings("unchecked")
    public void createAction() {
        try {
            Class<?> eventClass = Class.forName(this.actionClassName);
            this.action = (CardAction<DamageCard>) eventClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            this.action = null;
        }
    }


    public void setAction(CardAction<DamageCard> action) {
        this.action = action;
    }

    public CardAction<DamageCard> getAction() {
        return this.action;
    }

}
