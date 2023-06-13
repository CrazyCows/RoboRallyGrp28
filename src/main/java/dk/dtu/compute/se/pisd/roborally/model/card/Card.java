package dk.dtu.compute.se.pisd.roborally.model.card;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.card.CardAction;

public abstract class Card extends Subject {

    String name;
    String effect;
    String imagePath;
    String actionClassName;

    public String getName() {
        return this.name;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getImagePath() {
        return this.imagePath;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**IntelliJ complains, I don't. Should never be called directly as it is an abstract class
     * @return returns null. This is an abstract class
     */
    public CardAction getAction() {
        return null;
    }
}
