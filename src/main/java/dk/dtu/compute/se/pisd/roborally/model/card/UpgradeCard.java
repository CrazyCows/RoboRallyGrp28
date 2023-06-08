package dk.dtu.compute.se.pisd.roborally.model.card;

import dk.dtu.compute.se.pisd.roborally.controller.card.CardAction;

import java.lang.reflect.InvocationTargetException;

public class UpgradeCard extends Card {

    CardAction<UpgradeCard> action;
    int cost;

    public UpgradeCard(String name, String effect, int cost, String imagePath, String actionClassName) {
        this.name = name;
        this.effect = effect;
        this.cost = cost;
        this.imagePath = imagePath;
        this.actionClassName = actionClassName;

    }

    @SuppressWarnings("unchecked")
    public void createAction() {
        try {
            Class<?> eventClass = Class.forName(this.actionClassName);
            this.action = (CardAction<UpgradeCard>) eventClass.getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            e.printStackTrace();
            this.action = null;
        }
    }


    public void setAction(CardAction<UpgradeCard> action) {
        this.action = action;
    }

    public CardAction<UpgradeCard> getAction() {
        return this.action;
    }

    public int getCost() {
        return this.cost;
    }


}
