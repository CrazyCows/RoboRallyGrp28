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
        if (!currentPlayer.hasCard("Firewall")){
            cardController.drawSpamCardToDiscardPile(currentPlayer); //Draws two spam damage cards
            cardController.drawSpamCardToDiscardPile(currentPlayer);
        } else {
            System.out.println("Player has a firewall installed and draws no spam cards.");
        }

        Space target = gameController.board.getRebootTokenSpace();
        if (target.getPlayer() != null){
            gameController.moveInDirection(target.getPlayer(),1,((RebootToken)target.getActions().get(0)).getHeading());
        }
        currentPlayer.setSpace(target);
        target.getActions().get(0).doAction(gameController,target);
        cardController.emptyProgram(currentPlayer);
        return false;
    }
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        CardController cardController = CardController.getInstance();
        Player currentPlayer = space.getPlayer();
        if (!currentPlayer.hasCard("Firewall")){
            cardController.drawSpamCardToDiscardPile(currentPlayer); //Draws two spam damage cards
            cardController.drawSpamCardToDiscardPile(currentPlayer);
        } else {
            System.out.println("Player has a firewall installed and draws no spam cards.");
        }

        Space target = gameController.board.getRebootTokenSpace();
        if (target.getPlayer() != null){
            gameController.moveInDirection(target.getPlayer(),1,((RebootToken)target.getActions()).getHeading());
        }
        currentPlayer.setSpace(target);
        target.getActions().get(0).doAction(gameController,target);
        cardController.emptyProgram(currentPlayer);
        return false;


        //Below is basically pseudocode for one way to handle how we could manage selecting direction
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
    }
}