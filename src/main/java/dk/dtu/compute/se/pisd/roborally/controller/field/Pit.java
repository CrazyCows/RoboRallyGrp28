package dk.dtu.compute.se.pisd.roborally.controller.field;

import dk.dtu.compute.se.pisd.roborally.controller.CardController;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;
import dk.dtu.compute.se.pisd.roborally.model.Player;

public class Pit extends FieldAction { //More generally just used for rebooting at this point
    private Heading heading;
    public Heading getHeading() {
        return heading;
    }
    public void setHeading(Heading heading) {
        this.heading = heading;
    }



    public boolean doAction(@NotNull GameController gameController, @NotNull Player currentPlayer) {
        CardController cardController = CardController.getInstance();
        cardController.drawSpamCardToDiscardPile(currentPlayer); //Draws two spam damage cards
        cardController.drawSpamCardToDiscardPile(currentPlayer);
        System.out.println(currentPlayer.getName() + " is now being rebooted");
        currentPlayer.setSpace(currentPlayer.startSpace);
        cardController.emptyProgram(currentPlayer);
        currentPlayer.setHeading(Heading.NORTH);
        return false;
    }
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        CardController cardController = CardController.getInstance();
        Player currentPlayer = space.getPlayer();
        cardController.drawSpamCardToDiscardPile(currentPlayer); //Draws two spam damage cards
        cardController.drawSpamCardToDiscardPile(currentPlayer);
        System.out.println(currentPlayer.getName() + " is now being rebooted");
        currentPlayer.setSpace(currentPlayer.startSpace);
        cardController.emptyProgram(currentPlayer);
        currentPlayer.setHeading(Heading.NORTH);


        // TODO: Below is basically pseudocode for one way to handle how we could manage selecting direction
        /*
        //Alternative to this is to let the choosing currentPlayer/client finish the entire game, and then push the new
        //game state to the server, which everyone would then receive. This would be easier but would mess up the
        //animations and such, because it would suddenly set the game to be somewhere else.
        if (gameController.localPlayer == currentPlayer){
            //ASK FOR ROBOT HEADING
            //Heading newHeading = askforheading;
            //currentPlayer.setHeading(newHeading);
            //PUSH HEADING SOMEWHERE (SPECIFIC FILE?)

        } else {
            //Repeatedly poll until direction has been pushed
            //currentPlayer.setHeading(newHeading); //newHeading from server
        }*/

        return false;
    }
}