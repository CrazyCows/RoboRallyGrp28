package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.CommandCardField;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.roborally.model.Player;

public class Pit extends FieldAction {
    private Heading heading;


    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    CardController cardController = CardController.getInstance();

    //Implement generics instead of overloading?
    public boolean doAction(@NotNull GameController gameController, @NotNull Player currentPlayer) {
        System.out.println(currentPlayer.getName() + " has fallen in a pit");
        cardController.addSpamCardToDiscardPile(currentPlayer); //Draws two spam damage cards
        cardController.addSpamCardToDiscardPile(currentPlayer);
        currentPlayer.setSpace(currentPlayer.startSpace);
        gameController.getCardController().moveProgramIntoDiscardPile(currentPlayer);
        currentPlayer.setHeading(Heading.NORTH);
        return false;
    }
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        Player currentPlayer = space.getPlayer();
        System.out.println(currentPlayer.getName() + " has fallen in a pit");
        cardController.addSpamCardToDiscardPile(currentPlayer); //Draws two spam damage cards
        cardController.addSpamCardToDiscardPile(currentPlayer);
        currentPlayer.setSpace(currentPlayer.startSpace);
        gameController.getCardController().moveProgramIntoDiscardPile(currentPlayer);
        currentPlayer.setHeading(Heading.NORTH);




        // TODO: Below is basically pseudocode for one way to handle how we could manage selecting direction
        /*
        //Alternative to this is to let the choosing currentPlayer/client finish the entire game, and then push the new
        //game state to the server, which everyone would then receive. This would be easier but would mess up the
        //animations and such, because it would suddenly set the game to be somewhere else.
        if (gameController.localPlayer == currentPlayer){
            //TODO: ASK FOR ROBOT HEADING
            //Heading newHeading = askforheading;
            //currentPlayer.setHeading(newHeading);
            //TODO: PUSH HEADING SOMEWHERE (SPECIFIC FILE?)

        } else {
            //TODO: Repeatedly poll until direction has been pushed
            //currentPlayer.setHeading(newHeading); //newHeading from server
        }*/

        return false;
    }
}